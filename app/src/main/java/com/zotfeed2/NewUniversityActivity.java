package com.zotfeed2;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


public class NewUniversityActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Activity activity;
    private ViewPager viewPager;
    private TabLayout tabs;
    private String getQuery;
    private EditText text;
    private static ArrayList<Article> feeds = new ArrayList<Article>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_new_university);
        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabs = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(viewPager);
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
                        if (menuItem.getTitle().equals("KUCI")) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else if (menuItem.getTitle().equals("New University")) {
                            Intent intent = new Intent(getApplicationContext(), NewUniversityActivity.class);
                            startActivity(intent);
                        } else if (menuItem.getTitle().equals("AnteaterTV")) {
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

        searchIntent(getIntent());

    }
    @Override
    protected void onNewIntent(Intent intent) {
        searchIntent(intent);
    }
    // Add Fragments to Tabs
    private void setupViewPager(final ViewPager viewPager) {
        final Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(ListContentFragment.newInstance("http://www.newuniversity.org/category/news/feed/"), "News");
        adapter.addFragment(ListContentFragment.newInstance("http://www.newuniversity.org/category/entertainment/feed/"), "A & E");
        adapter.addFragment(ListContentFragment.newInstance("http://www.newuniversity.org/category/features/feed/"), "Features");
        adapter.addFragment(ListContentFragment.newInstance("http://www.newuniversity.org/category/sports/feed/"), "Sports");
        adapter.addFragment(ListContentFragment.newInstance("http://www.newuniversity.org/category/opinion/feed/"), "Opinion");
        ///adapter.addFragment(new ListContentFragment(), "Arts");
        viewPager.setAdapter(adapter);

//        final Bundle bundle = new Bundle();
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if(position == 0){
//                    bundle.putString("link", "http://www.newuniversity.org/category/news/feed/");
//                    ListContentFragment fragment = new ListContentFragment();
//                    fragment.setArguments(bundle);
//                    adapter.addFragment(fragment, "News");
//                }
//                else if (position == 1){
//                    bundle.putString("link", "http://www.newuniversity.org/category/entertainment/feed/");
//                    ListContentFragment fragment = new ListContentFragment();
//                    fragment.setArguments(bundle);
//                    adapter.addFragment(fragment, "A & E");
//                }else if(position == 2){
//                    bundle.putString("link", "http://www.newuniversity.org/category/features/feed/");
//                    ListContentFragment fragment = new ListContentFragment();
//                    fragment.setArguments(bundle);
//                    adapter.addFragment(fragment, "Features");
//                }   else if (position == 3){
//                    bundle.putString("link", "http://www.newuniversity.org/category/sports/feed/");
//                    ListContentFragment fragment = new ListContentFragment();
//                    fragment.setArguments(bundle);
//                    adapter.addFragment(fragment, "Sports");
//                }   else if (position == 4){
//                    bundle.putString("link", "http://www.newuniversity.org/category/opinion/feed/");
//                    ListContentFragment fragment = new ListContentFragment();
//                    fragment.setArguments(bundle);
//                    adapter.addFragment(fragment, "Opinion");
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private Bundle bundle = new Bundle();
        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
//            switch(position){
//                case 0:
//                    bundle.putString("link", "http://www.newuniversity.org/category/news/feed/");
//                    ListContentFragment fragment = new ListContentFragment();
//                    fragment.setArguments(bundle);
//                    return fragment;
//                case 1:
//                    bundle.putString("link", "http://www.newuniversity.org/category/entertainment/feed/");
//                    ListContentFragment fragment1 = new ListContentFragment();
//                    fragment1.setArguments(bundle);
//                    return fragment1;
//                case 2:
//                    bundle.putString("link", "http://www.newuniversity.org/category/features/feed/");
//                    ListContentFragment fragment2 = new ListContentFragment();
//                    fragment2.setArguments(bundle);
//                    return fragment2;
//                case 3:
//                    bundle.putString("link", "http://www.newuniversity.org/category/opinion/feed/");
//                    ListContentFragment fragment3 = new ListContentFragment();
//                    fragment3.setArguments(bundle);
//                    return fragment3;
//                case 4:
//                    bundle.putString("link", "http://www.newuniversity.org/category/entertainment/feed/");
//                    ListContentFragment fragment4 = new ListContentFragment();
//                    fragment4.setArguments(bundle);
//                    return fragment4;
//                default:
//                    return null;
//            }
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
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        MenuItem searchitem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchitem);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), NewUniversityActivity.class)));
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
    private void searchIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            getQuery = query;
        }
    }
}
