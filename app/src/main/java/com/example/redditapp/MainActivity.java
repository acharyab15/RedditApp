package com.example.redditapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.redditapp.model.Feed;
import com.example.redditapp.model.entry.Entry;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "https://www.reddit.com/r/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed();

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                // Log.d(TAG, "onResponse: feed: " + response.body().toString());
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                List<Entry> entries = response.body().getEntries();

//                Log.d(TAG, "\n onResponse: entries: " + entries);
//                Log.d(TAG, "\n onResponse: author: " + entries.get(0).getAuthor().getName());
//                Log.d(TAG, "\n onResponse: updated: " + entries.get(0).getUpdated());
//                Log.d(TAG, "\n onResponse: title: " + entries.get(0).getTitle());

                // Extract href and images from each entry
                for (int i=0; i< entries.size(); i++) {
                    ExtractXML extractXML1 = new ExtractXML(entries.get(0).getContent(), "<a href=");
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML(entries.get(0).getContent(), "<img src=");
                    try{
                        postContent.add(extractXML2.start().get(0));
                    }catch (NullPointerException e){
                        postContent.add(null);
                        Log.e(TAG, "onResponse: NullPointerException(thumbnail): " + e.getMessage());


                    }catch (IndexOutOfBoundsException e){
                        postContent.add(null);
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException(thumbnail): " + e.getMessage());

                    }
                }

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(MainActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
