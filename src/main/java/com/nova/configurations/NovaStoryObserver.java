package com.nova.configurations;

import org.jbehave.core.model.*;
import org.jbehave.core.reporters.StoryReporter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Saleem on 02/07/2017.
 */
public final class NovaStoryObserver {

    private static final NovaStoryObserver INSTANCE = new NovaStoryObserver();

    private static List<Story> failedStories = new ArrayList<>();

    private static Story story;

    private static final StoryReporter PROXY = (StoryReporter) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{StoryReporter.class}, new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("beforeStory")) {
                story = (Story) args[0];
                boolean givenStory = (boolean) args[1];
            }
            if (method.getName().equals("afterStory")) {
                boolean givenOrRestartingStory = (boolean) args[0];
                story = null;
            }
            if (method.getName().equals("failed")) {
                String step = args[0].toString();
                Throwable cause = (Throwable) args[1];
                failedStories.add(story);
            }
            return null;
        }
    });

    private NovaStoryObserver() {
    }

    public static synchronized NovaStoryObserver getObserver() {
        return INSTANCE;
    }

    public StoryReporter getReporter() {
        return PROXY;
    }

    public List<Story> getFailedStories() {
        return failedStories;
    }

}
