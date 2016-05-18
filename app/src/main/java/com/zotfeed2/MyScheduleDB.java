package com.zotfeed2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by CHIRAG on 4/24/2016.
 */
public class MyScheduleDB extends SQLiteOpenHelper {

    private final String TABLE_NAME = "schedule";
    private String dbFileName;
    public MyScheduleDB(Context context, String db_filename, int db_version){
        super(context, db_filename, null, 4);
        dbFileName = db_filename;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            String sql = "CREATE TABLE schedule (";
            sql += "showName TEXT, ";
            sql += "start_time INTEGER, ";
            sql += "end_time INTEGER, ";
            sql += "dayOfWeek TEXT)";
            db.execSQL(sql);
        }catch(Exception e){
            Log.e("Fail to create database", "onCreate: ", e);
        }
    }
    private SQLiteDatabase openDb(){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            return db;
        }catch(Exception e){
            Log.e("Fail to open database", "open: ", e);
            return null;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS schedule");
        onCreate(db);
    }
    private int getNewID() {
        try {
            int id = 1;

            SQLiteDatabase db = openDb();
            String sql = "SELECT MAX(id) FROM schedule";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int max_card_id = cursor.getInt(0);
                id = max_card_id + 1;
            }
            db.close();
            cursor.close();
            return id;

        } catch (Exception e) {
            return 1;
        }
    }
    public boolean insertSchedule(Schedule schedule){
        try {
            SQLiteDatabase db = openDb();
            ContentValues contentValues = new ContentValues();
            contentValues.put("showName", schedule.getShowName());
            contentValues.put("start_time", schedule.getStartTime());
            contentValues.put("end_time", schedule.getEndTime());
            contentValues.put("dayOfWeek", schedule.getDay());
            db.beginTransaction();
            try{
                db.insert(TABLE_NAME, "", contentValues);
                db.setTransactionSuccessful();
            }finally{
                db.endTransaction();
                db.close();
            }
            return true;
        }catch(Exception e){
            Log.e("Fail to insert schedule", "insertSchedule:" , e);
            return false;
        }
    }
    public String getScheduleInfo(String day, int time){
        String sql = String.format("SELECT * FROM schedule WHERE start_time <= " + time + " AND " + time + " < end_time" + " AND " + "dayOfWeek =  '"+ day + "';");

        SQLiteDatabase db = openDb();
        String showName = "";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            showName  = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return showName;
    }
    public ArrayList<Schedule> getScheduleOfDay(String day){
        String sql = String.format("SELECT DISTINCT(showName), start_time, end_time, dayOfWeek FROM schedule WHERE dayOfWeek =  '" + day + "' ORDER BY start_time;");
        ArrayList<Schedule> schedules = new ArrayList<Schedule>();;
        SQLiteDatabase db = openDb();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                Schedule schedule = new Schedule();
                schedule.setShowName(cursor.getString(cursor.getColumnIndex("showName")));
                schedule.setDay(cursor.getString(cursor.getColumnIndex("dayOfWeek")));
                schedule.setStartTime(cursor.getInt(cursor.getColumnIndex("start_time")));
                schedule.setEndTime(cursor.getInt(cursor.getColumnIndex("end_time")));
                schedules.add(schedule);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return schedules;
    }
 }
