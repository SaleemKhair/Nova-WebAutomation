package com.nova;

import com.nova.configurations.NovaEmbedder;
import com.nova.configurations.NovaEnv;
import com.nova.configurations.NovaStoryObserver;
import com.nova.reporter.NovaReporterBuilder;
import org.jbehave.core.InjectableEmbedder;
import org.jbehave.core.annotations.Configure;
import org.jbehave.core.annotations.UsingEmbedder;
import org.jbehave.core.annotations.spring.UsingSpring;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.spring.SpringAnnotatedEmbedderRunner;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.*;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumContext;
import org.jbehave.web.selenium.SeleniumContextOutput;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;

@RunWith(SpringAnnotatedEmbedderRunner.class)
@Configure(using = SeleniumConfiguration.class, pendingStepStrategy = FailingUponPendingStep.class)
@UsingEmbedder(embedder = NovaEmbedder.class, generateViewAfterStories = true, ignoreFailureInStories = true, storyTimeoutInSecs = 100000, metaFilters = {"-skip"})
@UsingSpring(resources = {"steps.xml"})
public class NovaStories extends InjectableEmbedder {

    private int retryCount = 2;

    public NovaStories() {
    }

    @Test
    public void run() throws Throwable {
        SeleniumContext seleniumContext = new SeleniumContext();
        Format novaObserver = new Format("NovaObserver") {
            @Override
            public StoryReporter createStoryReporter(FilePrintStreamFactory factory, StoryReporterBuilder storyReporterBuilder) {
                return NovaStoryObserver.getObserver().getReporter();
            }
        };

        Format novaJSONReporter = new Format("NovaJSONReporter") {
            @Override
            public StoryReporter createStoryReporter(FilePrintStreamFactory factory, StoryReporterBuilder storyReporterBuilder) {
                return new NovaReporterBuilder().withRedisPersist().withJsonReport("./").build();
            }
        };
        Format[] formats = new Format[]{new SeleniumContextOutput(seleniumContext), CONSOLE, novaObserver, novaJSONReporter};
        CrossReference crossReference = new CrossReference();
        StoryReporterBuilder reporterBuilder = new StoryReporterBuilder()
                .withCodeLocation(codeLocationFromClass(getClass())).withFailureTrace(true)
                .withFailureTraceCompression(true)
                .withFormats(formats).withCrossReference(crossReference.withJsonOnly());
        Configuration configuration = injectedEmbedder().configuration();
        configuration
                .useStoryControls(new StoryControls().doResetStateBeforeScenario(false))
                .useStoryLoader(new LoadFromClasspath(getClass()))
                .useStoryReporterBuilder(reporterBuilder);
        EmbedderControls embedderControls = injectedEmbedder().embedderControls();
        embedderControls
                .doIgnoreFailureInView(Integer.valueOf(NovaEnv.getEnv().getProperty("rerun.failed.stories.count")) > 0)
                .useThreads(Integer.valueOf(NovaEnv.getEnv().getProperty("threads")));

        if (configuration instanceof SeleniumConfiguration) {
            SeleniumConfiguration seleniumConfiguration = (SeleniumConfiguration) configuration;
            seleniumConfiguration.useSeleniumContext(seleniumContext);
        }

        runStoriesAsPaths(embedderControls);

    }

    private void runStoriesAsPaths(EmbedderControls embedderControls) {
        injectedEmbedder().runStoriesAsPaths(storyPaths());
        List<String> failedStoryPaths = getFailedStoryPaths();
        rerunFailedStories(embedderControls, failedStoryPaths);
        shutDownExecutorService();

    }

    private void shutDownExecutorService() {
        ((NovaEmbedder) injectedEmbedder()).shutDownExecutorServiceNow();
    }

    private void rerunFailedStories(EmbedderControls embedderControls, List<String> failedStoryPaths) {
        Integer rerunCount = Integer.valueOf(NovaEnv.getEnv().getProperty("rerun.failed.stories.count"));
        for (int retryCount = 1; retryCount <= rerunCount; retryCount++) {
            if (retryCount == rerunCount) {
                embedderControls.doIgnoreFailureInView(false);
                injectedEmbedder().useEmbedderControls(embedderControls);
            }
            injectedEmbedder().runStoriesAsPaths(failedStoryPaths);

        }

    }

    private List<String> getFailedStoryPaths() {
        return NovaStoryObserver.getObserver()
                .getFailedStories().stream().map(Story::getPath).collect(Collectors.toList());
    }


    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(codeLocationFromClass(this.getClass()).getFile(),
                asList("**/" + System.getProperty("storyFilter", "*") + ".story"), null);
    }
}
