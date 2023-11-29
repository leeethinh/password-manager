package com.thinhle.password_manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InfoAdapter extends RecyclerView.Adapter<LoginViewHolder>{
    private ArrayList<LoginInfo> infoList;
    private MainActivity mainActivity;

    public InfoAdapter(ArrayList<LoginInfo> infoList, MainActivity mainActivity){
        this.infoList = infoList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public LoginViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.login_list_entry,parent,false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new LoginViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LoginViewHolder holder, int position) {
        LoginInfo t = infoList.get(position);
        holder.companyTitle.setText(t.getCompanyTitle());
        holder.username.setText(t.getUsername());
        //  holder.noteText.setText(t.getNoteText());
        String maskedPassword = "*".repeat(8);
        holder.password.setText(maskedPassword);

    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }
}
