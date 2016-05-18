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

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides UI for the view with List.
 */
public class SchFragment extends Fragment {
    RecyclerView recyclerView;
    private String day;
    private final static String LINK_DAY = "day";
    public static SchFragment newInstance(String link){
        SchFragment fragment = new SchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LINK_DAY, link);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        day = getArguments().getString(LINK_DAY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        if(day != null) {

            System.out.println(day + "link here!!!");
            MyScheduleDB db = new MyScheduleDB(getContext(), "Schedules", 1);
            ArrayList<Schedule> schedules =  db.getScheduleOfDay(day);
            recyclerView.setAdapter(new ContentAdapter(schedules));
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
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
        private List<Schedule> list;
        LayoutInflater inflater;

        public ContentAdapter(ArrayList<Schedule> args) {
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
            final Schedule schedule = list.get(position);
            holder.title.setText(schedule.getShowName());
            int tempStart = schedule.getStartTime();
            int tempEnd = schedule.getEndTime();
            String AMPM = "PM";
            if(tempStart >= 12 && tempEnd >= 12){
                if(tempStart != 12)
                    tempStart = tempStart - 12;
                if(tempEnd != 12)
                    tempEnd = tempEnd - 12;
                holder.description.setText(tempStart+AMPM+" - " + tempEnd+AMPM);
            }else{
                AMPM = "AM";
                String extraPM = "PM";
                boolean noon = false;
                boolean toMidnight = false;
                if(tempStart == 0)
                    tempStart = 12;
                else if(tempStart > 12) {
                    toMidnight = true;
                    tempStart = tempStart - 12;
                }
                if(tempEnd == 0)
                    tempEnd = 12;
                else if(tempEnd == 12){
                    noon = true;
                }
                if(noon){
                    holder.description.setText(tempStart+AMPM+" - " + tempEnd+extraPM);
                }else if(toMidnight){
                    holder.description.setText(tempStart+extraPM+" - " + tempEnd+AMPM);
                }else{
                    holder.description.setText(tempStart+AMPM+" - " + tempEnd+AMPM );
                }

            }

            holder.thumbnail.setImageResource(R.drawable.kuci_logo);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getContext(), SchedulePopUp.class);
                    intent.putExtra("showName", schedule.getShowName());
                    intent.putExtra("day", schedule.getDay());
//                    intent.putExtra("time", tempStart+am_pm+" - " + tempEnd+am_pm);
                    startActivity(intent);
                }
            });

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }

}
