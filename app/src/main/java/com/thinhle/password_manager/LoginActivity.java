package com.thinhle.password_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailET, passwordET;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);
        progressBar = findViewById(R.id.progress_bar);
        loginBtn = findViewById(R.id.loginBTN);
        createAccountBtnTV = findViewById(R.id.create_account_text_view_btn);
        loginBtn.setOnClickListener((v)->loginUser());
        createAccountBtnTV.setOnClickListener((v)->startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
    }

    void loginUser(){
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        boolean isValidated = validateData(email,password);
        if(!isValidated){
            return;
        }
        LoginAccountInFireBase(email,password);
    }
    void  LoginAccountInFireBase(String email, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    //login successful
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        //go to main activity if verified email
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    } else{
                        Utility.showToast(LoginActivity.this,"Email not verified.");

                    }

                } else{ //login failed
                    Utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());

                }

            }
        });

    }
    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }

    }
    boolean validateData(String email, String password){

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordET.setError("Password cannot be fewer than 6 characters.");
            return false;
        }
        return true;
    }
}