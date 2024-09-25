package com.example.uniq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
public class LandingActivity extends AppCompatActivity
{

    String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);

        SharedPreferences sh = getSharedPreferences("Uniq", Context.MODE_PRIVATE);

        Email = sh.getString("email", "");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Email.isEmpty())
                {
                    Intent i = new Intent(LandingActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i = new Intent(LandingActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 3000);
    }
}