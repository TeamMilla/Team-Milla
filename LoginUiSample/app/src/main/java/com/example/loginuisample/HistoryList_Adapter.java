package com.example.loginuisample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HistoryList_Adapter extends FirestoreRecyclerAdapter<Location_Model, HistoryList_Adapter.HistoryHolder> {

    public HistoryList_Adapter(@NonNull FirestoreRecyclerOptions<Location_Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HistoryHolder holder, int position, @NonNull Location_Model model) {
        holder.Location_item.setText(model.getLocation());
        holder.Date_item.setText(model.getDate());
        holder.TimeIn_item.setText(model.getTimeIn());
        holder.TimeOut_item.setText(model.getTimeOut());

    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list,parent, false);
        return new HistoryHolder(view);
    }

    class HistoryHolder extends RecyclerView.ViewHolder{
        TextView Location_item,Date_item,TimeIn_item,TimeOut_item;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            Location_item = itemView.findViewById(R.id.title);
            Date_item = itemView.findViewById(R.id. date_h);
            TimeIn_item = itemView.findViewById(R.id. time_in_h);
            TimeOut_item = itemView.findViewById(R.id. time_out_h);
        }


    }
}
