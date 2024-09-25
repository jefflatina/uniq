package com.example.uniq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class HomeActivity extends AppCompatActivity {

    TextView tvFullName;
    CardView cardViewDetect;
    String Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        SharedPreferences sh = getSharedPreferences("Uniq", Context.MODE_PRIVATE);

        Email = sh.getString("email", "");

        tvFullName = findViewById(R.id.tvFullName);
        cardViewDetect = findViewById(R.id.cardViewDetect);

        cardViewDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, DetectActivity.class);
                startActivity(i);
            }
        });

        /*try {
            InputStream modelIn = getAssets().open("en_token.bin");
            TokenizerModel model = new TokenizerModel(modelIn);

            Tokenizer tokenizer = new TokenizerME(model);

            String sentence = "This is a sample sentence.";
            String[] tokens = tokenizer.tokenize(sentence);

            for (String token : tokens) {
                Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception err)
        {
            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
        }*/

        LoadAccount();
    }

    void LoadAccount()
    {

        String url = URLDatabase.URL_HOME;

        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(!response.equals("[]"))
                    {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                            String fullName = jsonObjectData.getString("full_name");
                            //String picture = jsonObjectData.getString("picture");

                            /*if(!picture.equals("null"))
                            {
                                byte[] decodedString = Base64.decode(picture, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                imgViewPicture.setImageBitmap(decodedByte);
                            }*/

                            tvFullName.setText("Hey " + fullName + "!");
                        }
                    }

                } catch (Exception e) {

                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", Email);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        queue.add(request);
    }

    @Override
    public void onBackPressed() {

    }
}