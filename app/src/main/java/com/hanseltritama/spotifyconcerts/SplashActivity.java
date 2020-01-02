package com.hanseltritama.spotifyconcerts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hanseltritama.spotifyconcerts.model.SpotifyAPI;
import com.hanseltritama.spotifyconcerts.model.User;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private Retrofit retrofit;
    private SpotifyAPI spotifyAPI;


    private RequestQueue queue; // Android Volley for queueing network call

    private static final String CLIENT_ID = "a8c4958fe607433eb5fd605fb9f264cd";
    private static final String REDIRECT_URI = "com.hanseltritama.spotifyconcerts://callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Let's remove title bar by overriding onCreate method
        // using Request Window Feature
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        spotifyAuth();

        mSharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        spotifyAPI = retrofit.create(SpotifyAPI.class);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {

                // Response was successful and contains auth token
                case TOKEN:
                    // Add key "token" to the sharedPreferences
                    editor = getSharedPreferences("SPOTIFY", 0).edit();

                    // store token to SharedPreferences or persistent storage
                    editor.putString("token", response.getAccessToken());
                    Log.d("STARTING", "ACCESS TOKEN GRANTED");
                    editor.apply();
                    //waitForUserInfo();
                    break;

                // Auth error flow
                case ERROR:
                    Log.d("ERROR", "ACCESS TOKEN NOT FOUND");
                    break;

                // Handle other cases
                default:
                    Log.d("ERROR", "Response Type: " + response.getType());
            }
        }
    }


    public void spotifyAuth() {

        // Open authentication request with our CLIENT_ID & response type is AUTH_TOKEN
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        // Requested scope(s) for user to grant
        builder.setScopes(new String[]{SCOPES});

        // Send request to Spotify
        AuthenticationRequest request = builder.build();

        // Spotify has their own Login Activity
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    public void waitForUserInfo() {

        Map<String, String> headers = new HashMap<>();
        String token = mSharedPreferences.getString("token", "");
        String auth = "Bearer " + token;
        headers.put("Authorization", auth);

        Call<User> call = spotifyAPI.getUserInfo(headers);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()) { // if call is not successful
                    Log.d("ERROR", "Code: " + response.code());
                    return;
                }

                startMainActivity();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("ERROR: ", t.getMessage());
            }
        });

    }

    public void startMainActivity() {
        Intent newintent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(newintent);
    }
}
