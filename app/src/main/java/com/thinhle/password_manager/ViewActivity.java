package com.thinhle.password_manager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.thinhle.password_manager.databinding.ActivityViewBinding;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ViewActivity extends AppCompatActivity {
    private ActivityViewBinding binding;
    int Position;






    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        Intent intent = getIntent();
        if (intent.hasExtra("INFO")) { //If Note object is passed from MA (onclick edit), setText fields to note values
            //    Position = (int) intent.getSerializableExtra("NOTE_POSITION");}
            LoginInfo editInfo = (LoginInfo) intent.getSerializableExtra("INFO");
            Position = (int) intent.getSerializableExtra("INFOPOS");
            //  originalNote = editNote; compare if change
            if (editInfo != null) {
                binding.viewCompany.setText(editInfo.getCompanyTitle());
                binding.viewUsername.setText(editInfo.getUsername());
                String hidden = "*".repeat(12);
                binding.viewPassword.setText(hidden);


            } else {
                return;}
            }
        }
    public void revealPassword(View view){
        Intent intent = getIntent();
        if (intent.hasExtra("INFO")){
            LoginInfo editInfo = (LoginInfo) intent.getSerializableExtra("INFO");
            if (editInfo != null) {
              //  binding.viewPassword.setText(editInfo.getPassword());
                try{
                String decryptedText = EncryptDecrypt.decrypt(editInfo.getPassword());
                binding.viewPassword.setText(decryptedText);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void doDelete(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Proceed with deletion?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent dataToReturn = new Intent();
                dataToReturn.putExtra("DELETE", true);
                dataToReturn.putExtra("INFOPOS", Position);
                setResult(RESULT_OK, dataToReturn);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() { //Do
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void doEdit(View v){
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter new Information:");
        builder.setTitle("Edit Data");


        EditText company = view.findViewById(R.id.dialogCompany);
        EditText username = view.findViewById(R.id.dialogUsername);
        EditText password = view.findViewById(R.id.dialogPassword);
        company.setText(binding.viewCompany.getText()); //display current data
        username.setText(binding.viewUsername.getText());
        password.setText(binding.viewPassword.getText());

        // Set the inflated view to be the builder's view
        builder.setView(view);
        builder.setPositiveButton("SAVE", (dialog, id) -> {
            String encryptedPassword = null;
            try {
                encryptedPassword = EncryptDecrypt.encrypt(password.getText().toString());
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            }
            //save then create new object
            Intent dataToReturn = new Intent();
            LoginInfo addInfo = new LoginInfo(company.getText().toString(), username.getText().toString(), encryptedPassword);

            dataToReturn.putExtra("INFO", addInfo);
            dataToReturn.putExtra("INFOPOS", Position);
            setResult(RESULT_OK, dataToReturn);
            finish();

        });
        builder.setNegativeButton("CANCEL", (dialog, id) -> {

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}



