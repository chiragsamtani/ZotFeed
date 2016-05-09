//package com.zotfeed2;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by CHIRAG on 5/5/2016.
// */
//    public class ContentAdapter extends RecyclerView.Adapter<ListContentFragment.sukgb mmmm> {
//    // Set numbers of List in RecyclerView.
//    private static final int LENGTH = 18;
//    private List<Article> list;
//    LayoutInflater inflater;
//
//    public ContentAdapter(LayoutInflater inflater, ArrayList<Article> articles) {
//        this.inflater = inflater;
//        this.list = articles;
//    }
//
//    @Override
//    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate()
//        ListViewHolder holder = new ListViewHolder(view);
//        holder.itemView = view;
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(ListViewHolder holder, int position) {
//        final Article article = list.get(position);
//        holder.title.setText(article.getTitle());
//        holder.description.setText(article.getDescription());
//        holder.thumbnail.setImageResource(R.mipmap.ic_launcher);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                String url = article.getUrl();
//                intent.setData(Uri.parse(url));
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    class ListViewHolder extends RecyclerView.ViewHolder {
//        TextView title, description;
//        ImageView thumbnail;
//
//        public ListViewHolder(View itemView) {
//            super(itemView);
//            title = (TextView) itemView.findViewById(R.id.list_title);
//            description = (TextView) itemView.findViewById(R.id.list_desc);
//            thumbnail = (ImageView) itemView.findViewById(R.id.list_avatar);
//
//        }
//
//    }
//}
//
//
