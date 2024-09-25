package com.example.uniq;

import java.util.ArrayList;
import java.util.List;

public class TextPreprocessor
{
    public static List<String> preprocess(String text)
    {
        String[] words = text.toLowerCase().split("\\s+");
        List<String> preprocessed = new ArrayList<>();

        for (String word : words) {
            // You can add more preprocessing steps here.
            preprocessed.add(word);
        }

        return preprocessed;
    }
}
