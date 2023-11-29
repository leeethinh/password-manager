package com.thinhle.password_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailET, passwordET, confirmPasswordET;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);
        confirmPasswordET = findViewById(R.id.confirmPasswordEditText);
        progressBar = findViewById(R.id.progress_bar);
        createAccountBtn = findViewById(R.id.createAccountBTN);
        loginTV = findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener(v->createAccount());
        loginTV.setOnClickListener(v->finish());
    }
    void createAccount(){
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();
        boolean isValidated = validateData(email,password,confirmPassword);
        if(!isValidated){
            return;
        }
        createAccountInFireBase(email,password);

    }
    void createAccountInFireBase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                         //  Utility.showToast(CreateAccountActivity.this, "Account Successfully Created");
                            Toast.makeText(CreateAccountActivity.this, "Account created! Please verify email address.", Toast.LENGTH_LONG).show();

                            firebaseAuth.getCurrentUser().sendEmailVerification(); //verify user first so we sign out
                            firebaseAuth.signOut();
                            finish();
                        }else{
                            //failure
                            Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });

    }
    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        } else{
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }

    }
    boolean validateData(String email, String password, String confirmPassword){

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordET.setError("Password cannot be fewer than 6 characters.");
            return false;
        }
        if(!password.equals(confirmPassword)){
            confirmPasswordET.setError("Password must be matching.");
            return false;
        }
        return true;
    }
}