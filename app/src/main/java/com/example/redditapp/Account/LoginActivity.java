package com.example.redditapp.Account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.redditapp.Comments.CommentsActivity;
import com.example.redditapp.FeedAPI;
import com.example.redditapp.R;
import com.example.redditapp.URLS;
import com.example.redditapp.model.Feed;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ProgressBar mProgressBar;
    private EditText mUsername;
    private EditText mPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started");

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        mPassword = (EditText) findViewById(R.id.input_password);
        mUsername = (EditText) findViewById(R.id.input_username);
        mProgressBar = (ProgressBar) findViewById(R.id.loginRequestLoadingProgressBar);
        mProgressBar.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Attempting to log in.");
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                if(!username.equals("") && !password.equals("")) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    login(username, password);
                }
            }
        });
    }

    private void login(final String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLS.LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        Call<CheckLogin> call = feedAPI.signIn(headerMap, username, username, password, "json" );
        call.enqueue(new Callback<CheckLogin>() {
            @Override
            public void onResponse(Call<CheckLogin> call, Response<CheckLogin> response) {
                try {
                    Log.d(TAG, "onResponse: Server Response: " + response.toString());

                    String modHash = response.body().getJson().getData().getModhash();
                    String cookie = response.body().getJson().getData().getCookie();
                    Log.d(TAG, "onResponse: modHash: " + modHash);
                    Log.d(TAG, "onResponse: cookie: " + cookie);

                    if(!modHash.equals("")) {
                        setSessionParams(username, modHash, cookie);
                        mProgressBar.setVisibility(View.GONE);
                        mUsername.setText("");
                        mPassword.setText("");
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // navigate back to previous activity
                        finish();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage() );
                }


            }

            @Override
            public void onFailure(Call<CheckLogin> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "An Error occured", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Save session params once login is successful
     * @param username
     * @param modHash
     * @param cookie
     */
    private void setSessionParams(String username, String modHash, String cookie) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = preferences.edit();

        Log.d(TAG, "setSessionParams: Storing session variables: \n" +
                         "username: " + username + "\n" +
                         "modHash: " + modHash + "\n" +
                         "cookie: " + cookie + "\n"
        );

        editor.putString("@string/SessionUsername" , username);
        editor.commit();

        editor.putString("@string/SessionModHash" , modHash);
        editor.commit();

        editor.putString("@string/SessionCookie" , cookie);
        editor.commit();
    }
}
