package com.zotfeed2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
//import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;


public class AnteaterTvActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ArrayList<String> pageToken;
    private FloatingActionButton prevButton;
    private FloatingActionButton nextButton;
    private static YouTube youtube;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anteater_tv);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Create Navigation drawer and inflate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.menubar);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        if(menuItem.getTitle().equals("New University")){
                            Intent intent = new Intent(getApplicationContext(), NewUniversityActivity.class);
                            startActivity(intent);
                        }else if(menuItem.getTitle().equals("KUCI")){
                            Intent intent = new Intent(getApplicationContext(), KUCIActivity.class);
                            startActivity(intent);
                        }
                        else if(menuItem.getTitle().equals("AnteaterTV")){
                        }
                        menuItem.setChecked(true);

                        // TODO: handle navigation

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        String videoParams = "videos";
        String playlistParams = "playlist";
        adapter.addFragment(CardContentFragment.newInstance(videoParams), "Videos");
        adapter.addFragment(CardContentFragment.newInstance(playlistParams), "Playlists");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

//    public void loadVideos(){
//       try{
//           //https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=UUbnxxWYkWtb4vpFRdkf3hzQ&key=AIzaSyBHpRVQOEXpfcn-CHxR5eQxqhbDdN0Xw-o
//           final Map<String, String> params = new HashMap<String, String>();
//           RequestQueue queue = Volley.newRequestQueue(this);
//           String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=UUbnxxWYkWtb4vpFRdkf3hzQ&key=AIzaSyBHpRVQOEXpfcn-CHxR5eQxqhbDdN0Xw-o";
//           JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(params),
//                   new Response.Listener<JSONObject>() {
//                       @Override
//                       public void onResponse(JSONObject response) {
//                           try {
//                               //System.out.println(response);
//                               JSONArray item = response.getJSONArray("items");
//                               String nextPageToken = response.getString("nextPageToken");
//                               ArrayList<VideoItem> videoBeans = new ArrayList<VideoItem>();
//                               for(int i = 0; i < item.length(); i++){
//                                   VideoItem videoItem = new VideoItem();
//                                   JSONObject oneItem = (JSONObject) item.getJSONObject(i);
//                                   JSONObject snippet = (JSONObject) oneItem.getJSONObject("snippet");
//
//                                   String title = snippet.getString("title");
//                                   videoItem.setTitle(title);
//
//                                   String description = snippet.getString("description");
//                                   videoItem.setDescription(description);
//
//                                   JSONObject thumbnail = (JSONObject) snippet.getJSONObject("thumbnails");
//                                   JSONObject highResImage = (JSONObject) thumbnail.getJSONObject("high");
//                                   String imageUrl = highResImage.getString("url");
//                                   videoItem.setImageUrl(imageUrl);
//
//                                   JSONObject resourcesId = (JSONObject) snippet.getJSONObject("resourceId");
//                                   String videoUrl = resourcesId.getString("videoId");
//                                   videoItem.setVideoUrl(videoUrl);
//                                   videoBeans.add(videoItem);
//
//                               }
//                           } catch (Exception e) {
//                               e.printStackTrace();
//                           }
//                       }
//                   }, new Response.ErrorListener() {
//               @Override
//               public void onErrorResponse(VolleyError error) {
//                   // handle error
//               }
//           }) {
//           };
//           queue.add(req);
//
//       }catch (Exception e){
//           e.printStackTrace();
//       }
//
//    }

}
