//package com.zotfeed2;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.api.client.http.HttpRequest;
//import com.google.api.client.http.HttpRequestInitializer;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.Channel;
//import com.google.api.client.auth.oauth2.Credential;
//
//import com.google.api.services.youtube.model.ChannelListResponse;
//import com.google.api.services.youtube.model.PlaylistItem;
//import com.google.api.services.youtube.model.PlaylistItemListResponse;
//import com.google.api.services.youtube.model.SearchListResponse;
//import com.google.api.services.youtube.model.SearchResult;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.List;
//
/////**
//// * Created by CHIRAG on 5/24/2016.
//// */
//public class YoutubeCheck {
//    private static final String API_KEY = "AIzaSyBHpRVQOEXpfcn-CHxR5eQxqhbDdN0Xw-o";
//    static YouTube youtube;
//    static YouTube.Search.List search;
//
//    public void  search(String query){
//        search.setQ(query);
//        try{
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//
//    public static void main(String[] args){
////        String url = "http://www.kuci.org/schedule.json";
////        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
////                new Response.Listener<String>() {
////                    @Override
////                    public void onResponse(JSON response) {
////                        if (!response.isEmpty()) {
////                            try {
////                                InputStream in = new ByteArrayInputStream(Charset.forName("UTF-8").encode(response).array());
////                                readJsonStream(in);
////                                System.out.print(response);
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    }
////                },
////                new Response.ErrorListener() {
////                    @Override
////                    public void onErrorResponse(VolleyError error) {
////                        error.printStackTrace();
////                    }
////                }) {
////
////        };
////        queue.add(req);
//
//        try {
//            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
//                @Override
//                public void initialize(HttpRequest httpRequest) throws IOException {
//
//                }
//            }).setApplicationName("ZotFeed").build();
////            search = youtube.search().list("id,snippet");
////            search.setKey(API_KEY);
////            search.setType("video");
////            search.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
////            search.setQ("");
////            SearchListResponse response = search.execute();
////            List<SearchResult> results = response.getItems();
////            for(SearchResult result : results){
////                System.out.println(result.getSnippet().getTitle());
////            }
//            YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
//            channelRequest.setForUsername("anteatertvtelevision");
//          //  channelRequest.setMine(true);
//            channelRequest.setKey("AIzaSyBHpRVQOEXpfcn-CHxR5eQxqhbDdN0Xw-o");
//            channelRequest.setPart("contentDetails");
//            channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
//            ChannelListResponse result = channelRequest.execute();
//
//            //should be just 1 item
//            List<Channel> channelList = result.getItems();
//            if (channelList != null) {
//                String uploadPlaylists = channelList.get(0).getContentDetails().getRelatedPlaylists().getUploads();
//                List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();
//                YouTube.PlaylistItems.List playListItemsGet = youtube.playlistItems().list("id,contentDetails,snippet");
//                playListItemsGet.setPlaylistId(uploadPlaylists);
//                playListItemsGet.setFields(
//                        "items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");
//                //nextToken represents one page of the uploaded list
//                //if nextToken is null that means end of videos
//                String nextToken = "";
//                int i = 0;
//                while (nextToken != null) {
//                    playListItemsGet.setPageToken(nextToken);
//                    PlaylistItemListResponse playlistItemResult = playListItemsGet.execute();
//                    playlistItemList.addAll(playlistItemResult.getItems());
//                    nextToken = playlistItemResult.getNextPageToken();
//                    System.out.println(playlistItemList.get(i++));
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }catch(Throwable t){
//            t.printStackTrace();
//        }
//
//    }
//}
