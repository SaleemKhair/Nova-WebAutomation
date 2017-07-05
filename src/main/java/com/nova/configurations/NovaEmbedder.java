package com.nova.configurations;

import org.jbehave.core.embedder.Embedder;

/**
 * Created by Saleem on 02/07/2017.
 */
public class NovaEmbedder extends Embedder {

    @Override
    protected void shutdownExecutorService() {
        //do nothing
    }

    public void shutDownExecutorServiceNow() {
        super.shutdownExecutorService();
    }

}
