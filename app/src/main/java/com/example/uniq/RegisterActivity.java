package com.example.uniq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    LinearLayout linearLayoutBack;

    CardView cardViewRegister;
    EditText txtFullName, txtEmail, txtPassword, txtConfirmPassword;
    TextView tvShowHide, tvConfirmShowHide;
    EditText txtBirthDate;

    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;

    Spinner spinnerGender;

    String[] genders = { "Male", "Female"};
    String Gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        linearLayoutBack = findViewById(R.id.linearLayoutBack);
        cardViewRegister = findViewById(R.id.cardViewRegister);

        txtFullName = findViewById(R.id.txtFullName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        tvShowHide = findViewById(R.id.tvShowHide);
        tvConfirmShowHide = findViewById(R.id.tvConfirmShowHide);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        spinnerGender = findViewById(R.id.spinnerGender);

        spinnerGender.setOnItemSelectedListener(RegisterActivity.this);

        ArrayAdapter ad = new ArrayAdapter(RegisterActivity.this, android.R.layout.simple_spinner_item, genders);

        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerGender.setAdapter(ad);

        tvShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvShowHide.getText().toString().equals("SHOW"))
                {
                    tvShowHide.setText("HIDE");
                    txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    tvShowHide.setText("SHOW");
                    txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        tvConfirmShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvConfirmShowHide.getText().toString().equals("SHOW"))
                {
                    tvConfirmShowHide.setText("HIDE");
                    txtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    tvConfirmShowHide.setText("SHOW");
                    txtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RegisterButtonWatcher();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RegisterButtonWatcher();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                RegisterButtonWatcher();
            }
        });
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RegisterButtonWatcher();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RegisterButtonWatcher();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                RegisterButtonWatcher();
            }
        });
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RegisterButtonWatcher();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RegisterButtonWatcher();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                RegisterButtonWatcher();
            }
        });
        txtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RegisterButtonWatcher();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RegisterButtonWatcher();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                RegisterButtonWatcher();
            }
        });

        txtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                //date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                date_time = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                                txtBirthDate.setText(date_time);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        cardViewRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString()))
                {

                    String url = URLDatabase.URL_CHECK_ACCOUNT;

                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

                    StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("user_id").equals("null") || jsonObject.getString("user_id").equals(""))
                                {

                                    Intent intent= new Intent(RegisterActivity.this, RegisterAuthenticationActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("VerificationCode", getRandomNumberString());
                                    bundle.putString("FullName", txtFullName.getText().toString());

                                    bundle.putString("Email", txtEmail.getText().toString());
                                    bundle.putString("Password", txtPassword.getText().toString());
                                    bundle.putString("BirthDate", txtBirthDate.getText().toString());
                                    bundle.putString("Gender", Gender);

                                    intent.putExtras(bundle);

                                    startActivity(intent);

                                }
                                else
                                {
                                    Toast.makeText(RegisterActivity.this, "Your email is already existing in the database.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                            params.put("email", txtEmail.getText().toString());
                            return params;
                        }
                    };
                    queue.add(request);
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Password and Confirm Password must be the same.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
    {
        Gender = genders[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    void RegisterButtonWatcher()
    {
        if(txtFullName.getText().toString().isEmpty() ||
                txtEmail.getText().toString().isEmpty() ||
                txtPassword.getText().toString().isEmpty() ||
                txtConfirmPassword.getText().toString().isEmpty())
        {
            cardViewRegister.setAlpha(0.2f);
            cardViewRegister.setFocusable(false);
            cardViewRegister.setClickable(false);
        }
        else
        {
            cardViewRegister.setAlpha(1);
            cardViewRegister.setFocusable(true);
            cardViewRegister.setClickable(true);
        }
    }
}