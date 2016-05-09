package com.zotfeed2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CHIRAG on 5/3/2016.
 */
public class Article implements Parcelable {
    private String title;
    private String url;
    private String pubDate;
    private String category;
    private String description;

    public Article() {
        this.title = "";
        this.url = "";
        this.pubDate = "";
        this.category = "";
        this.description = "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setPubDate(String date) {
        this.pubDate = date;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeString(pubDate);
        dest.writeString(category);
    }

    public Article(Parcel in) {
       this();
        readFromParcel(in);

    }
    public void readFromParcel(Parcel in){
        this.title = in.readString();
        this.url = in.readString();
        this.description = in.readString();
        this.pubDate = in.readString();
        this.category = in.readString();
    }

    public final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        public Article[] newArray(int size) {
            return new Article[size];

        }
    };
}
