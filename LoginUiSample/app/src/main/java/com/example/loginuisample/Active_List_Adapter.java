package com.example.loginuisample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class Active_List_Adapter extends FirestoreRecyclerAdapter<Location_Model, Active_List_Adapter.ActiveListHolder> {
    private OnItemClicklistener listener;
    public Active_List_Adapter(@NonNull FirestoreRecyclerOptions<Location_Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ActiveListHolder holder, int position, @NonNull Location_Model model) {
        holder.Status_item.setText(model.getStatus().toString());
        holder.Location_item.setText(model.getLocation());
        holder.Date_item.setText(model.getDate());
        holder.TimeIn_item.setText(model.getTimeIn());
        holder.TimeOut_item.setText(model.getTimeOut());

    }

    @NonNull
    @Override
    public ActiveListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_list,parent, false);
        return new ActiveListHolder(view);
    }

    class ActiveListHolder extends RecyclerView.ViewHolder{
        TextView Location_item,Date_item,TimeIn_item,TimeOut_item,Status_item;

        public ActiveListHolder(@NonNull View itemView) {
            super(itemView);
            Location_item = itemView.findViewById(R.id.title);
            Date_item = itemView.findViewById(R.id. date_h);
            TimeIn_item = itemView.findViewById(R.id. time_in_h);
            TimeOut_item = itemView.findViewById(R.id. time_out_h);
            Status_item = itemView.findViewById(R.id. status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }

    }
    public interface OnItemClicklistener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }
    public void setOnItemClickListner(OnItemClicklistener listener){
        this.listener = listener;
    }
}
