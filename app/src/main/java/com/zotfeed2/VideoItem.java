package com.zotfeed2;

/**
 * Created by CHIRAG on 5/28/2016.
 */
public class VideoItem {
    private String title;
    private String description;
    private String imageUrl;
    private String videoUrl;

    public VideoItem(){
        this.title = "";
        this.description = "";
        this.imageUrl = "";
        this.videoUrl = "";
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getImageUrl(){
        return imageUrl;
    }
    public String getVideoUrl(){
        return videoUrl;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setDescription(String desc){
        this.description = desc;
    }
    public void setImageUrl(String url){
        this.imageUrl = url;
    }
    public void setVideoUrl(String vURL){
        this.videoUrl = vURL;
    }

}
