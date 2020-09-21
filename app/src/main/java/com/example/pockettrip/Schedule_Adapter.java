package com.example.pockettrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Schedule_Adapter extends RecyclerView.Adapter<Schedule_Adapter.ViewHolder> {

    private ArrayList<String> itemList;
    private ArrayList<String> itemPrintList;
    private Context context;
    private View.OnClickListener onClickItem;
    private int selectedPosition = -1;

    public Schedule_Adapter(){
        super();
    }

    public Schedule_Adapter(Context context, ArrayList<String> itemList,ArrayList<String> itemPrintList, View.OnClickListener onClickItem) {
        this.context = context;
        this.itemList = itemList;
        this.itemPrintList = itemPrintList;
        this.onClickItem = onClickItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // context 와 parent.getContext() 는 같다.
        View view = LayoutInflater.from(context)
                .inflate(R.layout.date_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String item = itemList.get(position);
        String printItem = itemPrintList.get(position);

        holder.textview.setText(printItem);
        holder.textview.setTag(item);
        holder.textview.setOnClickListener(onClickItem);
        /*holder.textview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });

        if(selectedPosition == position){
            holder.textview.setBackgroundColor(Color.parseColor("#567845"));
            holder.textview.setTextColor(Color.parseColor("#ffffff"));
        }else{
            holder.textview.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.textview.setTextColor(Color.parseColor("#000000"));
        }*/
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textview;

        public ViewHolder(View itemView) {
            super(itemView);

            textview = itemView.findViewById(R.id.date_item_textView);
        }
    }
}
