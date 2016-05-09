package com.zotfeed2;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.Toast;

import com.zotfeed2.Article;
import com.zotfeed2.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RSSFeedController extends AsyncTask<String, Void, ArrayList<Article>> {
    ProgressDialog dialog;
    Article article = new Article();
    ArrayList<Article> feeds;
    HttpURLConnection conn;
    InputStream stream;
    URL url;
    String link;

    public RSSFeedController(String url) {
        this.link = url;
        doInBackground(url);
    }


    @Override
    protected ArrayList<Article> doInBackground(String... arg0) {
        try {
            url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            if (conn.getResponseCode() == -1) {
                //Log.d("Error in establishing connection", "Exiting thread");
            } else {
                stream = conn.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(stream, null);

                int eventType = xpp.getEventType();
                String text = "";
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        //don't do anything here but still need to handle case
                        feeds = new ArrayList<Article>();
                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            article = new Article();
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        text = xpp.getText();
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            feeds.add(article);
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            article.setTitle(text);
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            article.setUrl(text);
                        } else if (xpp.getName().equalsIgnoreCase("pubdate")) {
                            article.setPubDate(text);
                        } else if (xpp.getName().equals("category")) {
                            article.setCategory(text);
                        } else if (xpp.getName().equals("description")) {
                            article.setDescription(text);
                        }
                    }
                    eventType = xpp.next();

                }
                Set<String> set = new HashSet<String>();
                for (int i = 0; i < feeds.size(); i++) {
                    System.out.println(feeds.get(i).getCategory());
                    set.add(feeds.get(i).getCategory());
                }
                System.out.println(set.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return feeds;
    }

    @Override
    protected void onPostExecute(ArrayList<Article> result) {
        System.out.println("Done");
    }

    public ArrayList<Article> getItems(){
        if(feeds != null){
            return feeds;
        }else{
            return null;
        }
    }



}