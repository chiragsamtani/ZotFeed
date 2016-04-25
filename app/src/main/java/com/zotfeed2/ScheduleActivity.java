package com.zotfeed2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScheduleActivity extends AppCompatActivity {
    private Button button;
    private MyScheduleDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db =  new MyScheduleDB(this, "Schedules", 1);
        button = (Button) findViewById(R.id.parse);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream file = getResources().getAssets().open("schedule.json");
                    readJsonStream(file);
                }catch(Exception e) {
                    System.out.println(e);
                }
            }
        });

    }
    public void readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try{
            reader.beginArray();
            while(reader.hasNext()){
                readObject(reader);
            }
            reader.endArray();
        }finally{
            reader.close();
        }
    }
    public void readObject(JsonReader reader) throws IOException{
        Schedule schedule;
        MyScheduleDB db = new MyScheduleDB(this, "Schedules", 1);
        String showName = "";
        String start_time = "";
        String end_time = "";
        String day = "";
        int startTime = -1;
        int endTime = -1;
        reader.beginObject();

        while(reader.hasNext()){
            String tag = reader.nextName();
            if(tag.equals("title")){
                showName = reader.nextString();
            }
            else if(tag.equals("start_time")){
                start_time = reader.nextString();
                int index = start_time.indexOf(":");
                start_time = start_time.substring(0, index);
                //System.out.println(start_time);
            }
            else if(tag.equals("end_time")){
                end_time = reader.nextString();
                int index = end_time.indexOf(":");
                end_time = end_time.substring(0, index);
                //System.out.println(end_time);
            }
            else if(tag.equals("day")){
                day = reader.nextString();
            }
            else{
                reader.skipValue();
            }
            if(!start_time.isEmpty() || !start_time.equals("")) {
                startTime = Integer.parseInt(start_time);
                System.out.println(startTime);
            }
            if(!end_time.isEmpty() || !end_time.equals("")) {
                endTime = Integer.parseInt(end_time);
            }


//            System.out.println(endTime);
            schedule = new Schedule(showName, day, startTime, endTime);
            if(db.insertSchedule(schedule)){
                System.out.println("Good");
            }
        }
        reader.endObject();
    }
}


