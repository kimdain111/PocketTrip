package com.example.pockettrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Cash_Adapter extends RecyclerView.Adapter<Cash_Adapter.ViewHolder> {
    private ArrayList<String> itemList;
    private ArrayList<String> itemPrintList;
    private Context context;
    private View.OnClickListener onClickItem;

    public Cash_Adapter(Context context, ArrayList<String> itemList,ArrayList<String> itemPrintList, View.OnClickListener onClickItem) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = itemList.get(position);
        String printItem = itemPrintList.get(position);

        holder.textview.setText(printItem);
        holder.textview.setTag(item);
        holder.textview.setOnClickListener(onClickItem);
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