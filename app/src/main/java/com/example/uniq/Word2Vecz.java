package com.example.uniq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Word2Vecz
{
    private Map<String, double[]> wordVectors = new HashMap<>();
    private int vectorSize = 100;
    private double learningRate = 0.025;
    private int windowSize = 5;
    private int iterations = 10;
    private List<String> vocabulary = new ArrayList<>();



    public void train(List<String> sentences)
    {
        buildVocabulary(sentences);

        for (int iteration = 0; iteration < iterations; iteration++) {
            for (String sentence : sentences) {
                List<String> words = Arrays.asList(sentence.split(" "));

                for (int i = 0; i < words.size(); i++) {
                    String targetWord = words.get(i);
                    double[] targetVector = wordVectors.get(targetWord);

                    for (int j = Math.max(0, i - windowSize); j < Math.min(words.size(), i + windowSize); j++) {
                        if (i != j) {
                            String contextWord = words.get(j);
                            double[] contextVector = wordVectors.get(contextWord);
                            updateVectors(targetVector, contextVector);
                        }
                    }
                }
            }
        }
    }

    private void updateVectors(double[] target, double[] context) {
        // Calculate the error (the difference between the predicted and actual context vectors)
        double[] error = new double[vectorSize];
        for (int i = 0; i < vectorSize; i++) {
            error[i] = context[i] - target[i];
        }

        // Update the target vector using the error and the learning rate
        for (int i = 0; i < vectorSize; i++) {
            target[i] += learningRate * error[i];
        }

        // Update the context vector using the error and the learning rate
        for (int i = 0; i < vectorSize; i++) {
            context[i] -= learningRate * error[i];
        }
    }

    private void buildVocabulary(List<String> sentences) {
        for (String sentence : sentences) {
            List<String> words = Arrays.asList(sentence.split(" "));
            vocabulary.addAll(words.stream().distinct().collect(Collectors.toList()));
        }
        vocabulary = vocabulary.stream().distinct().collect(Collectors.toList());
        initializeWordVectors();
    }

    public double[] getAverageVector(String[] words) {
        double[] averageVector = new double[vectorSize]; // Assuming vectorSize is the size of your word vectors

        for (String word : words) {
            double[] wordVector = getWordVector(word);
            if (wordVector != null) {
                for (int i = 0; i < vectorSize; i++) {
                    averageVector[i] += wordVector[i];
                }
            }
        }

        // Normalize the average vector by dividing it by the number of words
        int wordCount = words.length;
        if (wordCount > 0) {
            for (int i = 0; i < vectorSize; i++) {
                averageVector[i] /= wordCount;
            }
        }

        return averageVector;
    }

    private void initializeWordVectors() {
        for (String word : vocabulary) {
            double[] vector = new double[vectorSize];
            for (int i = 0; i < vectorSize; i++) {
                vector[i] = Math.random();
            }
            wordVectors.put(word, vector);
        }
    }

    public double[] getWordVector(String word) {
        return wordVectors.get(word);
    }
}
