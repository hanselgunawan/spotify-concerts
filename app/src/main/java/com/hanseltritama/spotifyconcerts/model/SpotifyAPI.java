package com.hanseltritama.spotifyconcerts.model;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;

public interface SpotifyAPI {

    @GET("v1/me")
    Call<User> getUserInfo(@Header("Authorization") String token);

}
