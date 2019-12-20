package com.hanseltritama.spotifyconcerts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;

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
}
