package com.example.pockettrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder> {

    private ArrayList<CheckDTO> mData = null;

    public class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView checkItem;
        protected Button deleteBtn;

        public ViewHolder(View itemView){
            super(itemView);

            this.checkItem = itemView.findViewById(R.id.checkitem);
            this.deleteBtn = itemView.findViewById(R.id.deletecheck);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        mData.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    CheckAdapter(ArrayList<CheckDTO> list){
        mData = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.checklist_item, parent, false);
        CheckAdapter.ViewHolder vh = new CheckAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(CheckAdapter.ViewHolder holder, int position) {
        holder.checkItem.setText(mData.get(position).getCheckName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
