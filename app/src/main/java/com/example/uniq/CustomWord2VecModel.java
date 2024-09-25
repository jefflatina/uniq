package com.example.uniq;

import android.content.Context;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;

public class CustomWord2VecModel
{
    Context context;
    private final Word2Vec word2VecModel;

    public CustomWord2VecModel(Context context2, List<List<String>> datasets) {

        context = context2;

        // Combine all datasets into a single text corpus
        StringBuilder corpusBuilder = new StringBuilder();
        for (List<String> dataset : datasets) {
            for (String document : dataset) {
                corpusBuilder.append(document).append(" ");
            }
        }
        String corpus = corpusBuilder.toString();

        // Initialize a tokenizer factory
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();

        // Create a sentence iterator from the combined corpus
        SentenceIterator sentenceIterator = null;
        try {
            InputStream inputStream = context2.getAssets().open("documents/The Sherwood Foresters in the Great War 1914 - 1919 History of the 18th Battalion");
            sentenceIterator = new BasicLineIterator(inputStream);
        } catch (Exception e) {
            Toast.makeText(context2, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Configure Word2Vec model parameters
        word2VecModel = new Word2Vec.Builder()
                .layerSize(100)             // Set the size of word vectors
                .windowSize(5)              // Set the context window size
                .tokenizerFactory(tokenizerFactory)
                .iterate(sentenceIterator)  // Provide the sentence iterator
                .build();

        // Train the Word2Vec model
        word2VecModel.fit();
    }

    public Word2Vec getWord2VecModel() {
        return word2VecModel;
    }
}
