package com.zotfeed2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.Iterator;
import java.util.List;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


public class AnteaterTvActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private static YouTube youtube;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anteater_tv);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Create Navigation drawer and inflate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
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
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        else if(menuItem.getTitle().equals("AnteaterTV")){
                            Intent intent = new Intent(getApplicationContext(), AnteaterTvActivity.class);
                            startActivity(intent);
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
        adapter.addFragment(new CardContentFragment(), "Arts");
        adapter.addFragment(new CardContentFragment(), "Events");
        adapter.addFragment(new CardContentFragment(), "Music");
        adapter.addFragment(new CardContentFragment(), "Sports");
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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public static void loadVideos(){
        try{
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest httpRequest) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();
            YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
            channelRequest.setForUsername("anteatertvtelevision");
            channelRequest.setMine(true);
            channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
            ChannelListResponse result = channelRequest.execute();

            //should be just 1 item
            List<Channel> channelList = result.getItems();
            if(channelList != null){
                String uploadPlaylists = channelList.get(0).getContentDetails().getRelatedPlaylists().getUploads();
                List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();
                YouTube.PlaylistItems.List playListItemsGet = youtube.playlistItems().list("id,contentDetails,snippet");
                playListItemsGet.setPlaylistId(uploadPlaylists);
                playListItemsGet.setFields(
                        "items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");
                //nextToken represents one page of the uploaded list
                //if nextToken is null that means end of videos
                String nextToken = "";
                int i = 0;
                while(nextToken != null){
                    playListItemsGet.setPageToken(nextToken);
                    PlaylistItemListResponse playlistItemResult = playListItemsGet.execute();
                    playlistItemList.addAll(playlistItemResult.getItems());
                    nextToken = playlistItemResult.getNextPageToken();
                    System.out.println(playlistItemList.get(i++));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }catch(Throwable t){
            t.printStackTrace();
        }

    }


}
