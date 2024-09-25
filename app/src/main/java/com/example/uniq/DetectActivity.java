package com.example.uniq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class DetectActivity extends AppCompatActivity {

    LinearLayout linearLayoutBack;
    CardView cardViewUpload, cardViewCheck;
    EditText txtInput;
    CardView cardViewClear;
    public static int PICK_FILE = 1;

    private Map<String, String> documents;

    String mostSimilarDocument = "";

    String FileType = "";

    private Map<String, String> irregularVerbs;
    private Set<String> nameSet = new HashSet<String>();
    private Set<String> schoolSet = new HashSet<String>();

    String referenceTextDocument;

    Word2Vecz word2Vecz = new Word2Vecz();
    List<String> sentences = new ArrayList<String>();
    List<DatasetHelper> datasetHelpers;

    double FinalSimilarity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detect_activity);

        loadIrregularVerbs();

        try {
            datasetHelpers = new ArrayList<>();

            linearLayoutBack = findViewById(R.id.linearLayoutBack);
            cardViewUpload = findViewById(R.id.cardViewUpload);
            txtInput = findViewById(R.id.txtInput);
            cardViewClear = findViewById(R.id.cardViewClear);
            cardViewCheck = findViewById(R.id.cardViewCheck);

            linearLayoutBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            cardViewClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txtInput.setText("");
                }
            });

            cardViewUpload.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Dialog dialog = new Dialog(DetectActivity.this);

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setContentView(R.layout.file_upload_type_layout);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.BOTTOM;

                    window.setAttributes(wlp);

                    LinearLayout linearLayoutTextFile, linearLayoutPDF, linearLayoutDoc;

                    linearLayoutTextFile = dialog.findViewById(R.id.linearLayoutTextFile);
                    linearLayoutPDF = dialog.findViewById(R.id.linearLayoutPDF);
                    linearLayoutDoc = dialog.findViewById(R.id.linearLayoutDoc);

                    linearLayoutTextFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FileType = "TextFile";
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            startActivityForResult(intent, PICK_FILE);
                            dialog.dismiss();
                        }
                    });

                    linearLayoutPDF.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FileType = "PDF";
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent, PICK_FILE);
                            dialog.dismiss();
                        }
                    });

                    linearLayoutDoc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FileType = "Doc";
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                            startActivityForResult(intent, PICK_FILE);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            txtInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(txtInput.getText().toString().isEmpty())
                    {
                        cardViewCheck.setEnabled(false);
                        cardViewCheck.setClickable(false);
                        cardViewCheck.setFocusable(false);
                        cardViewCheck.setAlpha(0.2f);
                    }
                    else
                    {
                        cardViewCheck.setEnabled(true);
                        cardViewCheck.setClickable(true);
                        cardViewCheck.setFocusable(true);
                        cardViewCheck.setAlpha(1.0f);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            initializeDocuments();

            cardViewCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String document = identifySourceDocument(txtInput.getText().toString());

                    int Percentage = (int) (FinalSimilarity * 100);

                    Intent intent= new Intent(DetectActivity.this, ReportActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("Percentage",String.valueOf(Percentage));
                    bundle.putString("Document", document);
                    bundle.putString("ReferenceText", referenceTextDocument);

                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            });
        }
        catch (Exception err)
        {
            txtInput.setText(err.getMessage());
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
        stopWordsSet.add("a");
        stopWordsSet.add("about");
        stopWordsSet.add("above");
        stopWordsSet.add("after");
        stopWordsSet.add("again");
        stopWordsSet.add("against");
        stopWordsSet.add("ain");
        stopWordsSet.add("all");
        stopWordsSet.add("am");
        stopWordsSet.add("an");
        stopWordsSet.add("and");
        stopWordsSet.add("any");
        stopWordsSet.add("are");
        stopWordsSet.add("aren");
        stopWordsSet.add("aren't");
        stopWordsSet.add("as");
        stopWordsSet.add("at");
        stopWordsSet.add("be");
        stopWordsSet.add("because");
        stopWordsSet.add("been");
        stopWordsSet.add("before");
        stopWordsSet.add("being");
        stopWordsSet.add("below");
        stopWordsSet.add("between");
        stopWordsSet.add("both");
        stopWordsSet.add("but");
        stopWordsSet.add("by");
        stopWordsSet.add("can");
        stopWordsSet.add("couldn");
        stopWordsSet.add("couldn't");
        stopWordsSet.add("d");
        stopWordsSet.add("did");
        stopWordsSet.add("didn");
        stopWordsSet.add("didn't");
        stopWordsSet.add("do");
        stopWordsSet.add("does");
        stopWordsSet.add("doesn");
        stopWordsSet.add("doesn't");
        stopWordsSet.add("doing");
        stopWordsSet.add("don");
        stopWordsSet.add("don't");
        stopWordsSet.add("down");
        stopWordsSet.add("during");
        stopWordsSet.add("each");
        stopWordsSet.add("few");
        stopWordsSet.add("for");
        stopWordsSet.add("from");
        stopWordsSet.add("further");
        stopWordsSet.add("had");
        stopWordsSet.add("hadn");
        stopWordsSet.add("hadn't");
        stopWordsSet.add("has");
        stopWordsSet.add("hasn");
        stopWordsSet.add("hasn't");
        stopWordsSet.add("have");
        stopWordsSet.add("haven");
        stopWordsSet.add("haven't");
        stopWordsSet.add("having");
        stopWordsSet.add("he");
        stopWordsSet.add("her");
        stopWordsSet.add("here");
        stopWordsSet.add("hers");
        stopWordsSet.add("herself");
        stopWordsSet.add("him");
        stopWordsSet.add("himself");
        stopWordsSet.add("his");
        stopWordsSet.add("how");
        stopWordsSet.add("i");
        stopWordsSet.add("if");
        stopWordsSet.add("in");
        stopWordsSet.add("into");
        stopWordsSet.add("is");
        stopWordsSet.add("isn");
        stopWordsSet.add("isn't");
        stopWordsSet.add("it");
        stopWordsSet.add("it's");
        stopWordsSet.add("its");
        stopWordsSet.add("itself");
        stopWordsSet.add("just");
        stopWordsSet.add("ll");
        stopWordsSet.add("m");
        stopWordsSet.add("ma");
        stopWordsSet.add("me");
        stopWordsSet.add("mightn");
        stopWordsSet.add("mightn't");
        stopWordsSet.add("more");
        stopWordsSet.add("most");
        stopWordsSet.add("mustn");
        stopWordsSet.add("mustn't");
        stopWordsSet.add("my");
        stopWordsSet.add("myself");
        stopWordsSet.add("needn");
        stopWordsSet.add("needn't");
        stopWordsSet.add("no");
        stopWordsSet.add("nor");
        stopWordsSet.add("not");
        stopWordsSet.add("now");
        stopWordsSet.add("o");
        stopWordsSet.add("of");
        stopWordsSet.add("off");
        stopWordsSet.add("on");
        stopWordsSet.add("once");
        stopWordsSet.add("only");
        stopWordsSet.add("or");
        stopWordsSet.add("other");
        stopWordsSet.add("our");
        stopWordsSet.add("ours");
        stopWordsSet.add("ourselves");
        stopWordsSet.add("out");
        stopWordsSet.add("over");
        stopWordsSet.add("own");
        stopWordsSet.add("re");
        stopWordsSet.add("s");
        stopWordsSet.add("same");
        stopWordsSet.add("shan");
        stopWordsSet.add("shan't");
        stopWordsSet.add("she");
        stopWordsSet.add("she's");
        stopWordsSet.add("should");
        stopWordsSet.add("should've");
        stopWordsSet.add("shouldn");
        stopWordsSet.add("shouldn't");
        stopWordsSet.add("so");
        stopWordsSet.add("some");
        stopWordsSet.add("such");
        stopWordsSet.add("t");
        stopWordsSet.add("than");
        stopWordsSet.add("that");
        stopWordsSet.add("that'll");
        stopWordsSet.add("the");
        stopWordsSet.add("their");
        stopWordsSet.add("theirs");
        stopWordsSet.add("them");
        stopWordsSet.add("themselves");
        stopWordsSet.add("then");
        stopWordsSet.add("there");
        stopWordsSet.add("these");
        stopWordsSet.add("they");
        stopWordsSet.add("this");
        stopWordsSet.add("those");
        stopWordsSet.add("through");
        stopWordsSet.add("to");
        stopWordsSet.add("too");
        stopWordsSet.add("under");
        stopWordsSet.add("until");
        stopWordsSet.add("up");
        stopWordsSet.add("ve");
        stopWordsSet.add("very");
        stopWordsSet.add("was");
        stopWordsSet.add("wasn");
        stopWordsSet.add("wasn't");
        stopWordsSet.add("we");
        stopWordsSet.add("were");
        stopWordsSet.add("weren");
        stopWordsSet.add("weren't");
        stopWordsSet.add("what");
        stopWordsSet.add("when");
        stopWordsSet.add("where");
        stopWordsSet.add("which");
        stopWordsSet.add("while");
        stopWordsSet.add("who");
        stopWordsSet.add("whom");
        stopWordsSet.add("why");
        stopWordsSet.add("will");
        stopWordsSet.add("with");
        stopWordsSet.add("won");
        stopWordsSet.add("won't");
        stopWordsSet.add("wouldn");
        stopWordsSet.add("wouldn't");
        stopWordsSet.add("y");
        stopWordsSet.add("you");
        stopWordsSet.add("you'd");
        stopWordsSet.add("you'll");
        stopWordsSet.add("you're");
        stopWordsSet.add("you've");
        stopWordsSet.add("your");
        stopWordsSet.add("yours");
        stopWordsSet.add("yourself");
        stopWordsSet.add("yourselves");

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                if(FileType.equals("TextFile"))
                {
                    String fileContent = readTextFile(uri);
                    txtInput.setText(fileContent);
                }
                else if(FileType.equals("PDF"))
                {
                    try {

                        String parsedText="";
                        PdfReader reader = new PdfReader(this.getContentResolver().openInputStream(uri));
                        int n = reader.getNumberOfPages();
                        for (int i = 0; i <n ; i++) {
                            parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i+1).trim()+"\n"; //Extracting the content from the different pages
                        }
                        txtInput.setText(parsedText);
                        reader.close();
                    } catch (Exception e) {
                        txtInput.setText(e.getMessage());
                    }
                }

                else if(FileType.equals("Doc"))
                {
                    try {

                        String parsedText="";
                        DocumentConverter documentConverter = new DocumentConverter();

                        Result result = documentConverter.extractRawText(this.getContentResolver().openInputStream(uri));
                        parsedText = String.valueOf(result.getValue());
                        txtInput.setText(parsedText);
                    } catch (Exception e) {
                        txtInput.setText(e.getMessage());
                    }
                }
            } else {

            }
        }
    }

    private String readTextFile(Uri uri){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    public String identifySourceDocument(String text1) {
        double maxSimilarity = 0.0;
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
                referenceTextDocument = test.getContent();
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
}