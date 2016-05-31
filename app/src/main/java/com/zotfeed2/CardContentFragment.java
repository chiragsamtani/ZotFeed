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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides UI for the view with Cards.
 */
public class CardContentFragment extends Fragment {
    ArrayList<VideoItem> videoBeans = new ArrayList<VideoItem>();
    ProgressDialog dialog;
    private boolean search = false;
    private static String COMMAND = "command";
    private String getCommand = "";
    RecyclerView recyclerView;
    AlertDialog alertDialog;
    Constants constants;
    public static CardContentFragment newInstance(String request){
        CardContentFragment fragment = new CardContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COMMAND, request);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getCommand = getArguments().getString(COMMAND);
        constants = (Constants) getActivity().getApplicationContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        if(constants.isNetworkAvailable()) {
            dialog = new ProgressDialog(getContext());
            dialog.setIndeterminate(true);
            dialog.setMessage("Loading Anteater TV Videos");
            dialog.show();
            recyclerView.setAdapter(new ContentAdapter(null));
            if(getCommand.equals("videos") || getCommand.contains("v")) {
                loadVideos("");
            }else if(getCommand.equals("playlist") || getCommand.contains("p")){
                loadPlaylists("");
            }else if (search) {
                recyclerView.setAdapter(new ContentAdapter(videoBeans));
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

        }else{
            showDialog();
        }
        setHasOptionsMenu(true);
        return recyclerView;
    }
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_anteater_tv, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);
        if(sv != null){
            sv.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    System.out.print("QUERY:" + query);
                    ArrayList<VideoItem> results = new ArrayList<VideoItem>();
                    if (!videoBeans.isEmpty() || videoBeans != null) {
                        for (VideoItem videoItem : videoBeans) {
                            //System.out.println("Title: " + article.getTitle());
                            if (videoItem.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                results.add(videoItem);
                            }
                        }
                        System.out.println("Size: " + results.size());
                        search = true;
                        recyclerView.setAdapter(new ContentAdapter(results));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.isEmpty() || newText.equals(" ")) {
                        if (videoBeans != null) {
                            recyclerView.setAdapter(new ContentAdapter(videoBeans));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }
                    }
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);

    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            description = (TextView) itemView.findViewById(R.id.card_text);
            thumbnail = (ImageView) itemView.findViewById(R.id.card_image);
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
        private List<VideoItem> list;
        LayoutInflater inflater;

        public ContentAdapter(ArrayList<VideoItem> args) {
            this.list = args;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            System.out.println(list.size());
            System.out.println("position: " + position);
            final VideoItem video = list.get(position);
            holder.title.setText(video.getTitle());
            if(video.getDescription().isEmpty() || video.getDescription().equals("")){
                video.setDescription("No description found");
            }
            holder.description.setText(video.getDescription());
            //TODO: get image from URL
            //Use picasso library to load the images from the URL
            Picasso.with(getContext()).load(video.getImageUrl()).into(holder.thumbnail);
            //build URL given videoId
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(setUrlWithParams(video.getVideoUrl()));
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            if(list == null)
                return 0;
            else
                return list.size();
        }
    }
    protected void loadVideos(final String pageToken){
        try{
            System.out.println("hello");
            //https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=UUbnxxWYkWtb4vpFRdkf3hzQ&key=AIzaSyBHpRVQOEXpfcn-CHxR5eQxqhbDdN0Xw-o
            final Map<String, String> params = new HashMap<String, String>();
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&pageToken="+pageToken+"&maxResults=50&playlistId=UUbnxxWYkWtb4vpFRdkf3hzQ&key=AIzaSyBHpRVQOEXpfcn-CHxR5eQxqhbDdN0Xw-o";
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //System.out.println(response);
                                JSONArray item = response.getJSONArray("items");
                                String nextPageToken = response.getString("nextPageToken");


                                for(int i = 0; i < item.length(); i++){
                                    VideoItem videoItem = new VideoItem();
                                    JSONObject oneItem = (JSONObject) item.getJSONObject(i);
                                    JSONObject snippet = (JSONObject) oneItem.getJSONObject("snippet");

                                    String title = snippet.getString("title");
                                    videoItem.setTitle(title);

                                    String description = snippet.getString("description");
                                    videoItem.setDescription(description);

                                    JSONObject thumbnail = (JSONObject) snippet.getJSONObject("thumbnails");
                                    JSONObject highResImage = (JSONObject) thumbnail.getJSONObject("high");
                                    String imageUrl = highResImage.getString("url");
                                    videoItem.setImageUrl(imageUrl);

                                    JSONObject resourcesId = (JSONObject) snippet.getJSONObject("resourceId");
                                    String videoUrl = resourcesId.getString("videoId");
                                    videoItem.setVideoUrl(videoUrl);
                                    videoBeans.add(videoItem);

                                }
                                //recursively call volley to get nextPageTokens
                                if(nextPageToken != null) {
                                    loadVideos(nextPageToken);
                                }
                                dialog.dismiss();
                                recyclerView.setAdapter(new ContentAdapter(videoBeans));
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.invalidate();
                                System.out.println(videoBeans.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // handle error
                    error.printStackTrace();
                }
            }) {
            };
            queue.add(req);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    protected void loadPlaylists(final String pageToken){
        try{
            final Map<String, String> params = new HashMap<String, String>();
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId=UCbnxxWYkWtb4vpFRdkf3hzQ&pageToken="+pageToken+"&key=AIzaSyBHpRVQOEXpfcn-CHxR5eQxqhbDdN0Xw-o";
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //System.out.println(response);
                                JSONArray item = response.getJSONArray("items");
                                String nextPageToken = response.getString("nextPageToken");


                                for(int i = 0; i < item.length(); i++){
                                    VideoItem videoItem = new VideoItem();
                                    JSONObject oneItem = (JSONObject) item.getJSONObject(i);
                                    JSONObject snippet = (JSONObject) oneItem.getJSONObject("snippet");

                                    //overwrite videoUrl to be playlist Url
                                    String playlistId = oneItem.getString("id");
                                    videoItem.setVideoUrl(playlistId);

                                    String title = snippet.getString("title");
                                    videoItem.setTitle(title);

                                    //overwrite description for publishDate since playlists have no description
                                    String publishDate = snippet.getString("publishedAt");
                                    //cuts off string to only include date and not the time of the publis
                                    publishDate = publishDate.split("T")[0];
                                    videoItem.setDescription("Published at: " + publishDate);

                                    JSONObject thumbnail = (JSONObject) snippet.getJSONObject("thumbnails");
                                    JSONObject highResImage = (JSONObject) thumbnail.getJSONObject("high");
                                    String imageUrl = highResImage.getString("url");
                                    videoItem.setImageUrl(imageUrl);

                                    videoBeans.add(videoItem);

                                }
                                //recursively call volley to get nextPageTokens to retrieve next results
                                //since maxResults = 50 for one token
                                loadPlaylists(nextPageToken);
                                dialog.dismiss();
                                recyclerView.setAdapter(new ContentAdapter(videoBeans));
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.invalidate();
                                System.out.println(videoBeans.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // handle error
                    error.printStackTrace();
                }
            }) {
            };
            queue.add(req);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    protected Uri setUrlWithParams(String param){
        if(getCommand.equals("video") || getCommand.contains("v")) {
            String youtubeLink = "https://www.youtube.com/watch?v=";
            youtubeLink = youtubeLink + param;
            Uri uri = Uri.parse(youtubeLink);
            return uri;
        }else{
            String playlistLink = "https://www.youtube.com/playlist?list=";
            playlistLink = playlistLink + param;
            Uri uri = Uri.parse(playlistLink);
            return uri;
        }
    }
    protected void showDialog(){
        alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Internet Connection")
                .setMessage("You are not connected to Internet. Please check your connection settings and try again")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;
                        // dialog.cancel();
                        // getActivity().getSupportFragmentManager().popBackStack();
                    }
                }).create();
        alertDialog.show();

    }
}
