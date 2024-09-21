package com.bot.chatNovaAi.model;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.Dropout;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;

public class NovaAiConfig {

    private static final int embeddingSize = 100; // Adjust based on your embeddings
    private static final int hiddenLayerSize = 256; // Size of LSTM layers
    private static final int vocabularySize = 10000; // Size of your vocabulary

    public static MultiLayerConfiguration createModelConfiguration() {
        return new NeuralNetConfiguration.Builder()
                .updater(new Adam(0.001))
                .list()
                .layer(0, new LSTM.Builder()
                        .nIn(embeddingSize)
                        .nOut(hiddenLayerSize)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new Dropout(0.5)) // Dropout layer
                .layer(2, new LSTM.Builder()
                        .nIn(hiddenLayerSize)
                        .nOut(hiddenLayerSize)
                        .activation(Activation.TANH)
                        .build())
                .layer(3, new Dropout(0.5)) // Another dropout layer
                .layer(4, new LSTM.Builder()
                        .nIn(hiddenLayerSize)
                        .nOut(hiddenLayerSize)
                        .activation(Activation.TANH)
                        .build())
                .layer(5, new Dropout(0.5)) // Dropout layer
                .layer(6, new LSTM.Builder()
                        .nIn(hiddenLayerSize)
                        .nOut(hiddenLayerSize)
                        .activation(Activation.TANH)
                        .build())
                .layer(7, new OutputLayer.Builder()
                        .nIn(hiddenLayerSize)
                        .nOut(vocabularySize)
                        .activation(Activation.SOFTMAX)
                        .build())
                .build();
    }
}
