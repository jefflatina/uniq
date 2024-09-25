package com.example.uniq;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

//import org.deeplearning4j.models.word2vec.Word2Vec;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestActivity extends AppCompatActivity {
    TextView tvTest;
    Word2Vecz word2Vecz = new Word2Vecz();
    List<String> sentences = new ArrayList<String>();
    List<DatasetHelper> datasetHelpers;

    double FinalSimilarity;
    private Map<String, String> irregularVerbs;
    private Set<String> nameSet = new HashSet<String>();
    private Set<String> schoolSet = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tvTest = findViewById(R.id.tvTest);

        datasetHelpers = new ArrayList<>();

        loadIrregularVerbs();

        initializeDocuments();

        String test = identifySourceDocument("This period saw several important changes in personnel.");

        tvTest.setText(String.valueOf(test) + String.valueOf(FinalSimilarity));
    }

    private void initializeDocuments() {
        try {
            String[] fileList = getAssets().list("documents");
            for (String file : fileList) {
                String content = readDocument("documents/" + file);
                sentences.add(content);
                datasetHelpers.add(new DatasetHelper(file,content));
            }

            word2Vecz.train(sentences);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readDocument(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString().toLowerCase();
    }

    public String identifySourceDocument(String text1) {
        double maxSimilarity = -1.0;
        String sourceDocument = "";

        for(DatasetHelper test : datasetHelpers)
        {
            String textInput = text1.replaceAll("\n", " ");
            {

                textInput = removeStopWord(textInput);
                textInput = lemmatize(textInput);
                textInput = removeNames(textInput);
                textInput = removeSchools(textInput);
            }

            String textInput2 = test.getContent().replaceAll("\n", " ");
            {
                textInput2 = removeStopWord(textInput2);
                textInput2 = lemmatize(textInput2);
                textInput2 = removeNames(textInput2);
                textInput2 = removeSchools(textInput2);
            }

            String[] words1 = textInput.split("\\s+");
            String[] words2 = textInput2.split("\\s+");

            double[] vector1 = word2Vecz.getAverageVector(words1);
            double[] vector2 = word2Vecz.getAverageVector(words2);

            double similarity = cosineSimilarity(vector1, vector2);
            if (similarity > maxSimilarity)
            {
                maxSimilarity = similarity;
                FinalSimilarity = similarity;
                sourceDocument = test.getDocument();
            }
        }

        // Calculate cosine similarity between the vectors
        return sourceDocument;
    }

    private double cosineSimilarity(double[] vec1, double[] vec2) {
        // Calculate cosine similarity between two vectors
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        double similarity = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
        return similarity;
    }

    public void loadIrregularVerbs()
    {
        irregularVerbs = new HashMap<>();
        String content = readDocument("models/lemmatization_en.txt");
        String textStr[] = content.split("\\r\\n|\\n|\\r");

        for(String text : textStr)
        {
            String[] separated = text.split("\t");
            irregularVerbs.put(separated[1], separated[0]);
        }
    }

    public String removeStopWord(String text)
    {
        StringBuilder finalText = new StringBuilder();
        String[] words = text.split(" ");
        ArrayList<String> wordsList = new ArrayList<String>();
        Set<String> stopWordsSet = new HashSet<String>();
        stopWordsSet.add("THE");
        stopWordsSet.add("IS");
        stopWordsSet.add("AND");

        for(String word : words)
        {
            String wordCompare = word.toUpperCase();
            if(!stopWordsSet.contains(wordCompare))
            {
                wordsList.add(word);
            }
        }

        for (String str : wordsList){
            finalText.append(str).append(" ");
        }

        return finalText.toString();
    }

    public String lemmatize(String word) {
        if (irregularVerbs.containsKey(word)) {
            return irregularVerbs.get(word);
        }
        return word;
    }

    public String removeNames(String text)
    {
        StringBuilder finalText = new StringBuilder();
        String[] words = text.split(" ");

        loadNames();

        ArrayList<String> wordsList = new ArrayList<String>();

        for(String word : words)
        {
            String wordCompare = word.toUpperCase();
            if(!nameSet.contains(wordCompare))
            {
                wordsList.add(word);
            }
        }

        for (String str : wordsList){
            finalText.append(str).append(" ");
        }

        return finalText.toString();
    }

    public String removeSchools(String text)
    {
        StringBuilder finalText = new StringBuilder();
        String[] words = text.split(" ");

        loadSchools();

        ArrayList<String> wordsList = new ArrayList<String>();

        for(String word : words)
        {
            String wordCompare = word.toUpperCase();
            if(!schoolSet.contains(wordCompare))
            {
                wordsList.add(word);
            }
        }

        for (String str : wordsList){
            finalText.append(str).append(" ");
        }

        return finalText.toString();
    }

    public void loadNames()
    {
        String content = readDocument("models/names.txt");
        String textStr[] = content.split("\\r\\n|\\n|\\r");

        for(String text : textStr)
        {
            nameSet.add(text);
        }
    }

    public void loadSchools()
    {
        String content = readDocument("models/schools.txt");
        String textStr[] = content.split("\\r\\n|\\n|\\r");

        for(String text : textStr)
        {
            schoolSet.add(text);
        }
    }
}