package com.thinhle.password_manager;



import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LoginViewHolder extends RecyclerView.ViewHolder {
    TextView companyTitle;
    TextView username;
    TextView password;


    public LoginViewHolder(@NonNull View itemView) {
        super(itemView);
        companyTitle =itemView.findViewById(R.id.entry_editName);
        username =itemView.findViewById(R.id.entry_username);
        password =itemView.findViewById(R.id.entry_password);
    }
}
