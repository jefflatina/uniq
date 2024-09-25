package com.example.uniq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReportActivity extends AppCompatActivity {

    CircularProgressIndicator circularProgressIndicator;
    LinearLayout linearLayoutBack;
    TextView tvPercentage, tvDescription, tvPlagiarizedDocument, tvPlagiarizedDocumentContent;

    String Percentage, Document, ReferenceText;
    CardView cardViewDownload, cardViewPlagiarizedDocument;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        Percentage = getIntent().getExtras().getString("Percentage");
        Document = getIntent().getExtras().getString("Document");
        ReferenceText = getIntent().getExtras().getString("ReferenceText");

        linearLayoutBack = findViewById(R.id.linearLayoutBack);
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvDescription = findViewById(R.id.tvDescription);
        tvPlagiarizedDocument = findViewById(R.id.tvPlagiarizedDocument);
        cardViewDownload = findViewById(R.id.cardViewDownload);
        cardViewPlagiarizedDocument = findViewById(R.id.cardViewPlagiarizedDocument);
        tvPlagiarizedDocumentContent = findViewById(R.id.tvPlagiarizedDocumentContent);

        int plagiarizedPercentage = 100 - Integer.parseInt(Percentage);

        if(plagiarizedPercentage > 85)
        {
            cardViewPlagiarizedDocument.setVisibility(View.GONE);
        }

        cardViewDownload.setVisibility(View.GONE);

        circularProgressIndicator.setProgress(plagiarizedPercentage);
        tvPercentage.setText(plagiarizedPercentage + "%");

        tvDescription.setText(String.valueOf(Percentage) + "% Plagiarized Content" );

        Document = Document.substring(0, (Document.length() - 4));
        tvPlagiarizedDocument.setText(Document);

        tvPlagiarizedDocumentContent.setText(ReferenceText);

        cardViewDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(intent);
                        return;
                    }
                    takeScreenshot();
                }
            }
        });

        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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


    void saveTheBitmap(Bitmap bitmap)
    {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "test.jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();

            cardViewDownload.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception err)
        {
            cardViewDownload.setVisibility(View.VISIBLE);
        }
    }

    public Bitmap getScreenBitmap() {
        View v= findViewById(android.R.id.content).getRootView();
        v.setDrawingCacheEnabled(true);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false); // clear drawing cache
        return b;
    }

    public void takeScreenshot(){
        cardViewDownload.setVisibility(View.GONE);
        Bitmap bitmap = getScreenBitmap(); // Get the bitmap
        saveTheBitmap(bitmap);               // Save it to the external storage device.
    }

}