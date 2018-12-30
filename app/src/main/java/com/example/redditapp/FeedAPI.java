package com.example.redditapp;

import com.example.redditapp.Account.CheckLogin;
import com.example.redditapp.model.Feed;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FeedAPI {
    String BASE_URL = "https://www.reddit.com/r/";

    // Non-static feed name
    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);

    // Static feed name
    // @Get("earthporn/.rss")
    //Call(<Feed> getFeed());

    @POST("{user}")
    Call<CheckLogin> signIn(
       @HeaderMap Map<String, String> headers,
       @Path("user") String username,
       @Query("user") String user,
       @Query("passwd") String password,
       @Query("api_type") String type
    );
}
