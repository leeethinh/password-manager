package com.thinhle.password_manager;

import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.thinhle.password_manager.databinding.ActivityEditBinding;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class EditActivity extends AppCompatActivity {
    private ActivityEditBinding binding;
    EditText editCompany;
    EditText editUsername;
    EditText editPassword;
    EditText editConfirmPassword;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editCompany = binding.editName;
        editUsername = binding.editUsername;
        editPassword = binding.editPassword;
        editConfirmPassword = binding.editConfirmPassword;


        binding.editConfirmPassword.setOnKeyListener(new View.OnKeyListener() { //press enter on confirm password saves info
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    try {
                        saveInfo(v);
                    } catch (InvalidAlgorithmParameterException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchPaddingException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalBlockSizeException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (BadPaddingException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }
                return false;
            }
        });

    }
    public void saveInfo(View view) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String company = editCompany.getText().toString();
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        String encryptedText = EncryptDecrypt.encrypt(password);
        String confirmPassword = editConfirmPassword.getText().toString();
        if (company.isEmpty()|| username.isEmpty() || password.isEmpty()||confirmPassword.isEmpty()) {
            binding.errorText.setText("Please fill out the missing fields");
        }
        else if(!confirmPassword.equals(password)) {
            binding.errorText.setText("Password must be matching");
        } else {
                Intent dataToReturn = new Intent();
                LoginInfo addInfo = new LoginInfo(company, username, encryptedText);
                dataToReturn.putExtra("INFO", addInfo);
                //     dataToReturn.putExtra("POS", Position);
                setResult(RESULT_OK, dataToReturn);
                saveToFirebase(addInfo); //backupdata
                finish();
            }

    }

    void saveToFirebase(LoginInfo info){ //
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForData().document();

        documentReference.set(info).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    finish();
                } else{
                    Utility.showToast(EditActivity.this,"Failed adding Note");
                }
            }
        });

    }


    public void onBackPressed() {
        super.onBackPressed();

    }
}