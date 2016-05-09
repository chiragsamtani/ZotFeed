/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zotfeed2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides UI for the view with List.
 */
public class ListContentFragment extends Fragment {
    RecyclerView recyclerView;
    Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        RSSFeedController controller = new RSSFeedController("http://www.newuniversity.org/category/entertainment/feed/");
        controller.execute();
        return view;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            thumbnail = (ImageView) itemView.findViewById(R.id.list_avatar);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, DetailActivity.class);
//                    context.startActivity(intent);
//                }
//            });

        }
    }

    /**
     * Adapter to display recycler view.
     */
    class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;
        private List<Article> list;
        LayoutInflater inflater;

        public ContentAdapter(ArrayList<Article> args) {
            this.list = args;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            System.out.println(list.size());
            System.out.println("position: " + position);
            final Article article = list.get(position);
            holder.title.setText(article.getTitle());
            holder.description.setText(article.getDescription());
            holder.thumbnail.setImageResource(R.mipmap.ic_launcher);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String url = article.getUrl();
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class RSSFeedController extends AsyncTask<String, Void, ArrayList<Article>> {
        //ProgressDialog dialog;
        Article article = new Article();
        ArrayList<Article> feeds = new ArrayList<Article>();
        HttpURLConnection conn;
        InputStream stream;
        URL url;
        String link;

        public RSSFeedController(String url) {
            this.link = url;
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
            if(result != null) {
                System.out.println("Done");
                recyclerView.setAdapter(new ContentAdapter(result));
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        }
    }
}
