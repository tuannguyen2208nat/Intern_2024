package com.example.intern_2024.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intern_2024.R;
import com.example.intern_2024.model.list_auto;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;

import java.util.List;

public class AutoAdapter extends RecyclerView.Adapter<AutoAdapter.UserViewHolder> {

    private List<list_auto> listAutomations;
    private AutoAdapter.IClickListener mIClickListener;

    public interface IClickListener {
        void onClickEditAuto(list_auto auto);

        void onClickDeleteAuto(list_auto auto);

        void onClickUseAuto(list_auto auto, State state);
    }


    public AutoAdapter(List<list_auto> listAutomations, AutoAdapter.IClickListener mIClickListener) {
        this.listAutomations = listAutomations;
        this.mIClickListener = mIClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_automation, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        list_auto listAutoItem = listAutomations.get(position);
        if (listAutoItem == null) {
            return;
        }
        holder.auto_name.setText(String.valueOf(listAutoItem.getName()));

        holder.faceOffToggleButton.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton fotb) {
                if (state.equals(State.LEFT)) {
                    mIClickListener.onClickUseAuto(listAutoItem, State.LEFT);
                }
                if (state.equals(State.RIGHT)) {
                    mIClickListener.onClickUseAuto(listAutoItem, State.RIGHT);
                }
            }
        });

        holder.auto_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIClickListener.onClickEditAuto(listAutoItem);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (listAutomations != null) {
            return listAutomations.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView auto_name;
        private JellyToggleButton faceOffToggleButton;
        private ImageView auto_edit;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            auto_name = itemView.findViewById(R.id.auto_name);
            faceOffToggleButton = itemView.findViewById(R.id.auto_btn);
            auto_edit = itemView.findViewById(R.id.auto_edit);
        }
    }
}
