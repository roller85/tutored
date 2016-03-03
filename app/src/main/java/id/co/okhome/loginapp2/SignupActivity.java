package id.co.okhome.loginapp2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signupId, signupPassword, signupPasswordRe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        signupId = (EditText) findViewById(R.id.signup_id);
        signupPassword = (EditText) findViewById(R.id.signup_pw);
        signupPasswordRe = (EditText) findViewById(R.id.signup_pwRe);

        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (signupPassword.getText().toString().equals(signupPasswordRe.getText().toString())) {
                    attempSubmit();
                    break;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please Rewrite the Password", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }

    private void attempSubmit() {
        String id = signupId.getText().toString();
        String password = signupPassword.getText().toString();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            SubmitApiTask submitApiTask = new SubmitApiTask();
            submitApiTask.execute(id, password);
        }
        else {
            Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private class SubmitApiTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            String id = params[0];
            String password = params[1];

            try {

                URL url = new URL(ServerAPI.SIGNUP + "?id=" + id + "&password=" + password);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();

                byte[] buffer = new byte[8];
                int i;
                while ((i = in.read(buffer)) != -1) {
                    stringBuilder.append(new String(buffer, 0, i));
                }

                urlConnection.disconnect();

                String result = stringBuilder.toString();

                if (result.equals("Sign up complete")) {
                    return true;
                }
                else {
                    return false;
                }

            } catch (MalformedURLException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "Sign up Complete", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aBoolean);
        }
    }
}
