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
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;

import java.util.List;

public class RelayAdapter extends RecyclerView.Adapter<RelayAdapter.UserViewHolder> {
    private List<list_relay> listRelay;
    private IClickListener mIClickListener;

    public interface IClickListener {
        void onClickupdateRelay(list_relay relay);

        void onClickdeleteRelay(list_relay relay);

        void onClickuseRelay(list_relay relay, State state);
    }

    public RelayAdapter(List<list_relay> listRelay, IClickListener mIClickListener) {
        this.listRelay = listRelay;
        this.mIClickListener = mIClickListener;
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
        holder.relayIndex.setText(String.valueOf("Relay_" + listRelayItem.getRelay_id()));
        holder.relayName.setText(listRelayItem.getName());
//        holder.faceOffToggleButton.setChecked(change_to_boolean(listRelayItem.getState()));

        holder.change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIClickListener.onClickupdateRelay(listRelayItem);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIClickListener.onClickdeleteRelay(listRelayItem);
            }
        });
        holder.faceOffToggleButton.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton fotb) {
                if (state.equals(State.LEFT)) {
                    mIClickListener.onClickuseRelay(listRelayItem, State.LEFT);
                }
                if (state.equals(State.RIGHT)) {
                    mIClickListener.onClickuseRelay(listRelayItem, State.RIGHT);
                }
            }
        });

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
        private JellyToggleButton faceOffToggleButton;
        private ImageView deleteButton, change_name;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            relayIndex = itemView.findViewById(R.id.relay_index);
            relayName = itemView.findViewById(R.id.relay_name);
            change_name = itemView.findViewById(R.id.change_name);
            faceOffToggleButton = itemView.findViewById(R.id.relay_btn);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    private boolean change_to_boolean(int state) {
        if (state == 1) {
            return true;
        } else {
            return false;
        }
    }
}

