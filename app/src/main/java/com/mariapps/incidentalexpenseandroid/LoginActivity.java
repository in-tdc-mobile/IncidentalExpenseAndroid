package com.mariapps.incidentalexpenseandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import helperClass.AppConfig;
import helperClass.CommonFunctions;
import helperClass.HttpsTrustManager;
import helperClass.MyFirebaseInstanceIDService;
import helperClass.SessionManager;
import helperClass.URLs;

/**
 * Created by aruna.ramakrishnan on 11/23/2018.
 */

public class LoginActivity extends AppCompatActivity {

    Button btnSignIn;
    EditText edtEmail;
    EditText edtPassword;
    SessionManager sessionManager;
    CommonFunctions commonFunctions;
    LinearLayout linLayout;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        linLayout = (LinearLayout)  findViewById(R.id.linLayout);

        sessionManager = new SessionManager(LoginActivity.this);
        commonFunctions = new CommonFunctions(LoginActivity.this);
        progressDialog = new ProgressDialog(LoginActivity.this);

        if (sessionManager.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, MyFirebaseInstanceIDService.class);
            startService(intent);
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                // Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

    }


    private void checkLogin(final String email, final String password) {
        final String deviceId = UUID.randomUUID().toString();
        if (!CommonFunctions.isNetworkAvailable(getApplicationContext())) {
            commonFunctions.showSnackBar(linLayout, AppConfig.NO_INTERNET,"RETRY");
        }
        else{
            progressDialog.setMessage("Logging in ...");
            showDialog();
            JSONObject params = new JSONObject();
            try {
                params.put("UserName", email);
                params.put("Password", password);
                params.put("TokenId", sessionManager.getRegistrationId());
                params.put("DeviceType", "ANDROID");
                params.put("DeviceId", deviceId);
                params.put("AppCode", "DMA");
                params.put("AppVersion", String.valueOf(BuildConfig.VERSION_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("url",URLs.URL_LOGIN);
            Log.d("params",params.toString());

            final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            HttpsTrustManager.allowAllSSL();
            JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                    URLs.URL_LOGIN, params, //Not null.
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(final JSONObject jsonResponse) {
                            try {
                                if (commonFunctions.isUserAuthenticate(jsonResponse)) {
                                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                }

                                hideDialog();
                            } catch (Exception e) {
                                // JSON error
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("PANDA", "Error: " + error.getMessage());
                    Log.d("PANDA", error.toString());
                    hideDialog();
                    if (CommonFunctions.isNetworkAvailable(getApplicationContext())) {
                        commonFunctions.showSnackBar(linLayout, getString(R.string.invalid_credentials),"OK");
                    } else{
                        commonFunctions.showSnackBar(linLayout, AppConfig.NO_INTERNET,"RETRY");
                    }
                }
            });

            // Adding request to request queue
            strReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(strReq);

        }

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
