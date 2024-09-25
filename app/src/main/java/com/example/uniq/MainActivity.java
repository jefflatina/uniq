package com.example.uniq;

import androidx.appcompat.app.AppCompatActivity;

import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import org.deeplearning4j.models.word2vec.Word2Vec;

public class MainActivity extends AppCompatActivity {
    private Word2Vec word2VecModel;
    TextView tvTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTest = findViewById(R.id.tvTest);

        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
    }
    TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
    SentenceIterator iter;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            Uri selectedfile = data.getData(); //The uri with the location of the file


            try {

                tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
                //InputStream inputStream = getAssets().open("documents/The Sherwood Foresters in the Great War 1914 - 1919 History of the 18th Battalion.txt");
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(selectedfile);
                iter = new BasicLineIterator(inputStream);
                try {

                    Word2Vec vec = new Word2Vec.Builder()
                            .minWordFrequency(5)
                            .iterations(1)
                            .layerSize(100)
                            .seed(42)
                            .windowSize(5)
                            .iterate(iter)
                            .tokenizerFactory(tokenizerFactory)
                            //.vocabCache(new InMemoryLookupCache())
                            .build();

                    vec.fit();
                }
                catch (Exception err)
                {
                    tvTest.setText(err.getMessage());
                }

                // Train the Word2Vec model
                //vec.fit();
            } catch (Exception e) {
                tvTest.setText(e.getMessage());
            }
        }
    }
}