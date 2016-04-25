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
    private String day;

    public Schedule(String showName, String day, int start_time, int end_time){
        this.showName = showName;
        this.day = day;
        this.start_time = start_time;
        this.end_time = end_time;
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
    public static void main(String[] args){

    }


}
