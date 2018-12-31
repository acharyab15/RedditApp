package com.example.redditapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.redditapp.Account.LoginActivity;
import com.example.redditapp.Comments.CommentsActivity;
import com.example.redditapp.model.Feed;
import com.example.redditapp.model.entry.Entry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnRefreshFeed;
    private EditText mFeedName;
    private String currentFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting.");
        btnRefreshFeed = (Button) findViewById(R.id.btnRefreshFeed);
        mFeedName = (EditText) findViewById(R.id.etFeedName);

        setupToolbar();

        init();

        btnRefreshFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedName = mFeedName.getText().toString();
                if(!feedName.equals("")){
                    currentFeed = feedName;
                    init();
                }
                else{
                    init();
                }
            }
        });

    }



    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: clicked menu item: " + item);

                switch(item.getItemId()){
                    case R.id.navLogin:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                }
                return false;
            }
        });
    }

    private void init() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLS.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed(currentFeed);

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

                final ArrayList<Post> posts = new ArrayList<Post>();

                // Extract href and images from each entry
                for (int i=0; i< entries.size(); i++) {
                    ExtractXML extractXML1 = new ExtractXML(entries.get(i).getContent(), "<a href=");
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML(entries.get(i).getContent(), "<img src=");
                    try {
                        postContent.add(extractXML2.start().get(0));
                    } catch (NullPointerException e) {
                        postContent.add(null);
                        Log.e(TAG, "onResponse: NullPointerException(thumbnail): " + e.getMessage());


                    } catch (IndexOutOfBoundsException e) {
                        postContent.add(null);
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException(thumbnail): " + e.getMessage());

                    }
                    // image thumbnail is going to be in the last position
                    int lastPosition = postContent.size() - 1;

                    try{
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                entries.get(i).getAuthor().getName(),
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition),
                                entries.get(i).getId()
                        ));
                    }catch (NullPointerException e) {
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                "None",
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition),
                                entries.get(i).getId()
                        ));
                        Log.e(TAG, "onResponse: NullPointerEXception: " + e.getMessage());
                    }

                }

                for(int j = 0; j < posts.size(); j++) {
                    Log.d(TAG, "onResponse: \n " +
                            "PostURL: " + posts.get(j).getPostURL() + "\n " +
                            "ThumbnailURL: " + posts.get(j).getThumbnailURL() + "\n " +
                            "Title: " + posts.get(j).getTitle() + "\n " +
                            "Author: " + posts.get(j).getAuthor() + "\n " +
                            "Updated: " + posts.get(j).getDateUpdated() + "\n" +
                            "Post Id: " + posts.get(j).getId() + "\n");
                }

                ListView listView = (ListView) findViewById(R.id.listView);
                CustomListAdapter customListAdapter = new CustomListAdapter(MainActivity.this, R.layout.card_layout_main, posts);
                listView.setAdapter(customListAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemClick: Clicked" + posts.get(position).toString());
                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                        // allow the comments activity class to use the metadata info about the post
                        intent.putExtra("@string/post_url", posts.get(position).getPostURL());
                        intent.putExtra("@string/post_thumbnail", posts.get(position).getThumbnailURL());
                        intent.putExtra("@string/post_title", posts.get(position).getTitle());
                        intent.putExtra("@string/post_author", posts.get(position).getAuthor());
                        intent.putExtra("@string/post_updated", posts.get(position).getDateUpdated());
                        intent.putExtra("@string/post_id", posts.get(position).getId());

                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(MainActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }
}
