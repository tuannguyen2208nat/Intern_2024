package com.example.intern_2024.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.intern_2024.R;
import com.example.intern_2024.model.list_relay;
import com.github.angads25.toggle.widget.LabeledSwitch;
import java.util.List;

public class RelayAdapter extends RecyclerView.Adapter<RelayAdapter.UserViewHolder> {
    private List<list_relay> listRelay;

    public RelayAdapter(List<list_relay> listRelay) {
        this.listRelay = listRelay;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_relay, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        list_relay listRelayItem = listRelay.get(position);
        if (listRelayItem == null) {
            return;
        }
        holder.relayIndex.setText(String.valueOf(listRelayItem.getIndex()));
        holder.relayName.setText(listRelayItem.getName());
    }

    @Override
    public int getItemCount() {
        if (listRelay != null) {
            return listRelay.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView relayIndex, relayName;
        private LabeledSwitch labeledSwitch;
        private ImageView deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            relayIndex = itemView.findViewById(R.id.relay_index);
            relayName = itemView.findViewById(R.id.relay_name);
            labeledSwitch = itemView.findViewById(R.id.relay_btn);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}