package com.example.intern_2024.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intern_2024.R;
import com.example.intern_2024.model.list_relay;

import java.util.List;

public class RelayAutoAdapter extends RecyclerView.Adapter<RelayAutoAdapter.UserViewHolder> {

    private List<list_relay> listRelay;
    private IClickListener mIClickListener;

    public interface IClickListener {
        void onClickSelectRelay(list_relay relay, boolean isChecked);
    }

    public RelayAutoAdapter(List<list_relay> listRelay, IClickListener mIClickListener) {
        this.listRelay = listRelay;
        this.mIClickListener = mIClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_relay_auto, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        list_relay listRelayItem = listRelay.get(position);

        if (listRelayItem == null) {
            return;
        }

        holder.relay_name.setText(listRelayItem.getName());
        holder.checkBox.setOnCheckedChangeListener(null); // Unbind any previous li
        holder.checkBox.setChecked(listRelayItem.isChecked()); // Set the checkbox state
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listRelayItem.setChecked(isChecked); // Update the state in listRelayItem
                mIClickListener.onClickSelectRelay(listRelayItem, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRelay != null ? listRelay.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView relay_name;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_relay);
            relay_name = itemView.findViewById(R.id.relay_name);
        }
    }

    public void updateRelayAuto(List<list_relay> newRelayList) {
        listRelay.clear();
        listRelay.addAll(newRelayList);
        notifyDataSetChanged();
    }
}
