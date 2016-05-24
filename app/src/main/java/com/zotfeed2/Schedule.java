package com.zotfeed2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by CHIRAG on 4/24/2016.
 */
public class Schedule {
    private int start_time;
    private int end_time;
    private String showName;
    private String hosts;
    private String description;
    private String day;
    private String url;
    private String genre;
    public Schedule(){
        this.showName = "";
        this.day = "";
        this.start_time = -1;
        this.end_time = -1;
        this.hosts = "";
        this.description = "";
        this.url = "";
        this.genre = "";
    }
    public Schedule(String showName, String day, int start_time, int end_time, String host, String desc, String uri, String genr){
        this.showName = showName;
        this.day = day;
        this.start_time = start_time;
        this.end_time = end_time;
        this.hosts = host;
        this.description = desc;
        this.url = uri;
        this.genre = genr;
//        this.hosts = hosts;
//        this.description = description;
    }
    public void setURL(String url){
        this.url = url;
    }
    public void setGenre(String genre){
        this.genre = genre;
    }
    public void setHosts(String host){
        this.hosts = host;
    }
    public void setDescription(String desc){
        this.description = desc;
    }
    public void setShowName(String name){
        this.showName = name;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public void setStartTime(int startTime){
        this.start_time = startTime;
    }
    public void setEndTime(int endTime){
        this.end_time = endTime;
    }
    public String getShowName(){
        return showName;
    }
    public String getDay(){
        return day;
    }
    public int getStartTime(){
        return start_time;
    }
    public int getEndTime(){
        return end_time;
    }
    public String getHosts() {return hosts;}
    public String getDesc(){ return description;}
    public String getURL(){return url;
    }
    public String getGenre(){
        return genre;
    }
    public static void main(String[] args){

    }


}
