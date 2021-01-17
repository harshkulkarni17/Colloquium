package com.example.colloquium;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    RecyclerView recyclerView;
    Context context;
    ArrayList<String> items;
    ArrayList<String> urls;

    public void update(String name, String url){
        items.add(name);
        urls.add(url);
        notifyDataSetChanged(); //refreshes recycler view automatically
    }

    public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.nameOfFile.setText(items.get(position));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameOfFile;

        public ViewHolder(View itemView) {
            super(itemView);
            nameOfFile=itemView.findViewById(R.id.nameOfFile);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = recyclerView.getChildLayoutPosition(v);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //intent.setType(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(urls.get(position)), "*/*");
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e){
                        //
                    }

                }
            });
        }
    }

}
