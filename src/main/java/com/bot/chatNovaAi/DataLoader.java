package com.bot.chatNovaAi.data;

import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.iterator.SimpleDatasetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.util.*;

public class DataLoader {
    private static final String OOV_TOKEN = "<OOV>"; // Out-of-vocabulary token
    private List<String> sentences = new ArrayList<>();
    private List<String> intents = new ArrayList<>();
    private Map<String, Integer> wordIndex = new HashMap<>();
    private Map<Integer, String> indexWord = new HashMap<>();
    private Map<String, Integer> intentIndex = new HashMap<>();
    private int vocabSize;
    private int numIntents;

    public DataLoader(String filePath) {
        loadData(filePath);
        buildVocabulary();
    }

    private void loadData(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // Assuming CSV format: "sentence,intent"
                sentences.add(parts[0]);
                intents.add(parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildVocabulary() {
        Set<String> uniqueWords = new HashSet<>();
        Set<String> uniqueIntents = new HashSet<>();

        for (String sentence : sentences) {
            Collections.addAll(uniqueWords, sentence.toLowerCase().split("\\s+"));
        }
        for (String intent : intents) {
            uniqueIntents.add(intent);
        }

        // Add words to word index
        int index = 0;
        for (String word : uniqueWords) {
            wordIndex.put(word, index);
            indexWord.put(index, word);
            index++;
        }
        vocabSize = wordIndex.size();
        // Add OOV token
        wordIndex.put(OOV_TOKEN, vocabSize);
        indexWord.put(vocabSize, OOV_TOKEN);
        vocabSize++;

        // Add intents to intent index
        index = 0;
        for (String intent : uniqueIntents) {
            intentIndex.put(intent, index);
            index++;
        }
        numIntents = intentIndex.size();
    }

    public DataSetIterator getDataSetIterator(int batchSize, int maxSequenceLength) {
        List<INDArray> featureList = new ArrayList<>();
        List<INDArray> labelList = new ArrayList<>();

        for (int i = 0; i < sentences.size(); i++) {
            // Tokenize and encode sentences
            INDArray features = encodeSentence(sentences.get(i), maxSequenceLength);
            INDArray labels = encodeIntent(intents.get(i));
            featureList.add(features);
            labelList.add(labels);
        }

        INDArray featureArray = Nd4j.vstack(featureList);
        INDArray labelArray = Nd4j.vstack(labelList);

        return new SimpleDatasetIterator(new DataSet(featureArray, labelArray), batchSize);
    }

    private INDArray encodeSentence(String sentence, int maxLength) {
        String[] tokens = sentence.toLowerCase().split("\\s+");
        int[] indices = new int[maxLength];

        for (int i = 0; i < maxLength; i++) {
            if (i < tokens.length) {
                indices[i] = wordIndex.getOrDefault(tokens[i], wordIndex.get(OOV_TOKEN));
            } else {
                indices[i] = 0; // Padding index
            }
        }

        return Nd4j.create(indices, new int[]{1, maxLength}); // Shape: (1, maxLength)
    }

    private INDArray encodeIntent(String intent) {
        int intentIndexValue = intentIndex.getOrDefault(intent, -1);
        INDArray label = Nd4j.zeros(1, numIntents); // One-hot encoding
        if (intentIndexValue != -1) {
            label.putScalar(new int[]{0, intentIndexValue}, 1.0);
        }
        return label;
    }

    public Map<String, Integer> getWordIndex() {
        return wordIndex;
    }

    public Map<Integer, String> getIndexWord() {
        return indexWord;
    }

    public int getVocabSize() {
        return vocabSize;
    }

    public int getNumIntents() {
        return numIntents;
    }
}
