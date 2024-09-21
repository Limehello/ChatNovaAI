package com.bot.chatNovaAi.config;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.AttentionLayer;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;

public class NovaAiConfig {

    public static MultiLayerConfiguration createModelConfiguration(int inputSize, int vocabSize) {
        int numLayers = 6; // Number of transformer layers
        int numHeads = 8;  // Number of attention heads
        int dModel = 512;  // Dimensionality of the model
        int dff = 2048;    // Dimensionality of the feed-forward network
        double dropoutRate = 0.1; // Dropout rate

        // Build the model configuration
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(42) // For reproducibility
                .updater(new Adam(0.001)) // Learning rate
                .list();

        for (int i = 0; i < numLayers; i++) {
            // Add transformer block with attention and feed-forward layers
            configuration.layer(i, new AttentionLayer.Builder()
                    .nIn(dModel)
                    .nOut(dModel)
                    .numHeads(numHeads)
                    .dropOut(dropoutRate)
                    .activation(Activation.RELU)
                    .build());
            configuration.layer(i + numLayers, new FeedForwardLayer.Builder()
                    .nIn(dModel)
                    .nOut(dff)
                    .activation(Activation.RELU)
                    .dropOut(dropoutRate)
                    .build());
        }

        // Add output layer
        configuration.layer(numLayers * 2, new OutputLayer.Builder()
                .nIn(dff)
                .nOut(vocabSize) // Number of unique tokens/words
                .activation(Activation.SOFTMAX) // For classification
                .build());

        configuration.pretrain(false) // Disable pretraining
                .backprop(true) // Enable backpropagation
                .build();

        return configuration;
    }
}
