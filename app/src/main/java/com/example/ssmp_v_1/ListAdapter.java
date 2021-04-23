package com.example.ssmp_v_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {


    private List<List> oneList = new ArrayList<>();

    private static final String TWITTER_RESPONSE_FORMAT="EEE MMM dd HH:mm:ss ZZZZZ yyyy"; // Thu Oct 26 07:31:08 +0000 2017
    private static final String MONTH_DAY_FORMAT = "MMM d"; // Oct 26

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_list_table, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.bind(oneList.get(position));
    }

    @Override
    public int getItemCount() {
        return oneList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView directionDate;
        private TextView type;
        private TextView fullName;
        private TextView residence;
        private TextView client_id;
        private TextView action_id;
        private TextView status;
        private TextView time_and_fullName;
        private TextView birthDate;
        private TextView sex;
        private TextView snils;
        private TextView registration;
        private TextView contact;
        private TextView callNumberId;
        private TextView eventId;
        private TextView isDone;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            directionDate = itemView.findViewById(R.id.directionDate);
            type = itemView.findViewById(R.id.type);
            fullName = itemView.findViewById(R.id.fullName);
            residence = itemView.findViewById(R.id.residence);
            residence = itemView.findViewById(R.id.residence);
            client_id = itemView.findViewById(R.id.client_id);
            action_id = itemView.findViewById(R.id.action_id);
            status = itemView.findViewById(R.id.status);
            time_and_fullName = itemView.findViewById(R.id.time_and_fullName);
            birthDate = itemView.findViewById(R.id.birthDate);
            sex = itemView.findViewById(R.id.sex);
            snils = itemView.findViewById(R.id.snils);
            registration = itemView.findViewById(R.id.registration);
            contact = itemView.findViewById(R.id.contact);
            callNumberId = itemView.findViewById(R.id.callNumberId);
            eventId = itemView.findViewById(R.id.eventId);
            isDone = itemView.findViewById(R.id.isDone);
        }

        public void setItems(Collection<List> items) {
            oneList.addAll(items);
            notifyDataSetChanged();
        }

        public void clearItems() {
            oneList.clear();
            notifyDataSetChanged();
        }

        public void bind(List list) {
//            directionDate.setText(list);
        }
    }

}
