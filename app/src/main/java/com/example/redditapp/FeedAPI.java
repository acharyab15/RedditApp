package com.example.redditapp;

import com.example.redditapp.model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FeedAPI {
    String BASE_URL = "https://www.reddit.com/r/";
    @GET("earthporn/.rss")
    Call<Feed> getFeed();

}
