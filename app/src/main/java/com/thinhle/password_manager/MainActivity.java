package com.thinhle.password_manager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thinhle.password_manager.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {
    private ActivityMainBinding binding;
    private FirebaseUser currentUser;
    private String userUID;

    Toolbar myToolbar;
    private RecyclerView recyclerView;
    private InfoAdapter infoAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncherEdit;

    private final ArrayList<LoginInfo> infoList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userUID = currentUser.getUid();
            loadNotesFromInternalStorage(userUID);
            // Now 'userUID' contains the UID of the currently logged-in user
            // You can use this 'userUID' to associate data with this specific user
        } else {
            // No user is currently logged in
        }

        myToolbar =  binding.toolbar2;
        myToolbar.setTitle(R.string.app_name);
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);



        recyclerView = findViewById(R.id.infoRecycler);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing); // Define spacing in pixels
        recyclerView.addItemDecoration(new ItemSpacingDecoration(this, spacingInPixels)); //spacing

        infoAdapter = new InfoAdapter(infoList, this); //adapter
        recyclerView.setAdapter(infoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityResultLauncher = registerForActivityResult( //create new object
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
        activityResultLauncherEdit = registerForActivityResult( //click on existing object
                new ActivityResultContracts.StartActivityForResult(),
                this::handleEditResult);


    }
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        LoginInfo selectedInfo = infoList.get(pos);
        // Launch EditActivity to edit the selected note
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("INFO", selectedInfo);
        intent.putExtra("INFOPOS", pos);
        activityResultLauncherEdit.launch(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        LoginInfo t = infoList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete login info '" + t.getCompanyTitle()+ "'?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                int pos = recyclerView.getChildLayoutPosition(view);
                LoginInfo t = infoList.get(pos);
                infoList.remove(pos);
                myToolbar.setSubtitle("Data: "+infoList.size());
                infoAdapter.notifyItemRemoved(pos);
                saveNotesToInternalStorage(userUID);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() { //Do
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }
    public void handleEditResult(ActivityResult result){
        if(result==null||result.getData()==null){
            return;
        }
        if(result.getResultCode()==RESULT_OK){
            Intent dataResult= result.getData();
            if(dataResult.hasExtra("DELETE")){
                int pos = (int) dataResult.getSerializableExtra("INFOPOS");
                infoList.remove(pos);
                myToolbar.setSubtitle("Data: "+infoList.size());
                infoAdapter.notifyItemRemoved(pos);
                saveNotesToInternalStorage(userUID);
            }
            if(dataResult.hasExtra("INFO")){
                LoginInfo newInfo = (LoginInfo) dataResult.getSerializableExtra("INFO");
                int pos = (int) dataResult.getSerializableExtra("INFOPOS");
                infoList.remove(pos);
                infoList.add(pos, newInfo);
                infoAdapter.notifyItemRemoved(pos);
                infoAdapter.notifyItemInserted(pos);
                myToolbar.setSubtitle("Notes: " + infoList.size());
                saveNotesToInternalStorage(userUID);

            }
        } else if (result.getResultCode()==RESULT_CANCELED){
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Unexpected", Toast.LENGTH_SHORT).show();
        }

    }

    public void handleResult(ActivityResult result) { //Creating new object
        if (result == null || result.getData() == null) {
            return;
        }
        if (result.getResultCode() == RESULT_OK) {
            Intent dataResult = result.getData(); //get Intent
            if (dataResult.hasExtra("INFO")) {
                LoginInfo newInfo = (LoginInfo) dataResult.getSerializableExtra("INFO"); //get serializable Object passed from EditActivity
                infoList.add(0, newInfo);
                infoAdapter.notifyItemInserted(0);
                myToolbar.setSubtitle("Notes: " + infoList.size());
                saveNotesToInternalStorage(userUID);// Anytime note is saved/edited, save to storage

            }
        } else if (result.getResultCode() == RESULT_CANCELED) {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unexpected", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuA) { //Signout clicked
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you want to sign out?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() { //Do
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        } else if(item.getItemId()==R.id.menuC){ //Search is clicked
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);
            builder.setPositiveButton("OK", (dialog, id) -> {
                String querySearch = et.getText().toString().toLowerCase(); // Convert to lowercase for case-insensitive search
                // Perform search within the infoList based on company title
                ArrayList<LoginInfo> matchingResults = new ArrayList<>();
                ArrayList<LoginInfo> nonMatchingResults = new ArrayList<>();
                for (LoginInfo info : infoList) {
                    String companyTitle = info.getCompanyTitle().toLowerCase(); // Convert to lowercase for case-insensitive search
                    if (companyTitle.contains(querySearch)) {
                        // Found a partial match, add it to matchingResults
                        matchingResults.add(info);
                    } else {
                        // No match, add it to nonMatchingResults
                        nonMatchingResults.add(info);
                    }
                }
                // Combine the results: matching items first followed by non-matching
                matchingResults.addAll(nonMatchingResults);
                // Process the search results
                if (!matchingResults.isEmpty()) {
                    // Update the infoList with combined results
                    infoList.clear();
                    infoList.addAll(matchingResults);
                    // Notify adapter of changes
                    infoAdapter.notifyDataSetChanged();
                } else {
                    // No matches found, handle accordingly
                    Toast.makeText(MainActivity.this, "No matching results found", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, id) -> {
            });
            builder.setMessage("Search: ");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (item.getItemId() == R.id.menuB) { //save button
            //  Toast.makeText(this, "You want to add new Object", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, EditActivity.class);

            activityResultLauncher.launch(intent);
            return true;
        }
            return super.onOptionsItemSelected(item);

    }

    private void saveNotesToInternalStorage(String userUID) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (LoginInfo loginInfo : infoList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("companyTitle", loginInfo.getCompanyTitle());
                jsonObject.put("username", loginInfo.getUsername());
                jsonObject.put("password", loginInfo.getPassword());
                jsonObject.put("userUID", userUID);
                jsonArray.put(jsonObject);
            }

            String json = jsonArray.toString();

            FileOutputStream outputStream = openFileOutput(userUID + "_data.json", Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadNotesFromInternalStorage(String userUID) {
        try {
            FileInputStream inputStream = openFileInput(userUID + "_data.json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String storedUserUID = jsonObject.getString("userUID");
                if (storedUserUID.equals(userUID)) { // Check if data belongs to logged-in user
                    String companyTitle = jsonObject.getString("companyTitle");
                    String username = jsonObject.getString("username");
                    String password = jsonObject.getString("password");
                    LoginInfo data = new LoginInfo(companyTitle, username, password);
                    infoList.add(data);
                }
            }

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}