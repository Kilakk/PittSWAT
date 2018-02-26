package com.alexkdawson.cssdswat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.FileSystemNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public Button beginSessionButton;
    public Button settingsButton;
    public EditText buildingEditText;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO check these when button is clicked too
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},2);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_WIFI_STATE},3);
        }



        beginSessionButton = findViewById(R.id.beginSessionButton);
        buildingEditText = findViewById(R.id.buildingEditText);
        settingsButton = findViewById(R.id.settings_button_main);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String username = pref.getString("username", null);
        String type = pref.getString("type", null);
        String os = pref.getString("os", null);

        if(username == null || type == null || os == null){
            Intent settingsActIntent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(settingsActIntent);
            Toast.makeText(getApplicationContext(), "Please Enter Information About Device!",
                    Toast.LENGTH_LONG).show();
        }


        beginSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This Device does not have access to write to external storage!" +
                                    " Please enable this permission in settings!", Toast.LENGTH_LONG).show();
                }else {

                    pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    String username = pref.getString("username", null);
                    String type = pref.getString("type", null);
                    String os = pref.getString("os", null);

                    if (username == null || type == null || os == null) {
                        Intent settingsActIntent = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(settingsActIntent);
                        Toast.makeText(getApplicationContext(), "Please Enter Information About Device!",
                                Toast.LENGTH_LONG).show();
                    } else if (!buildingEditText.getText().toString().matches("")) {

                        Intent swatActIntent = new Intent(getBaseContext(), SwatActivity.class);
                        File outputFile = createSessionFile();
                        if(outputFile == null){
                            return;
                        }

                        swatActIntent.putExtra("buildingName", buildingEditText.getText().toString());
                        swatActIntent.putExtra("outputFile", outputFile);
                        swatActIntent.putExtra("username", username);
                        swatActIntent.putExtra("deviceType", type);
                        swatActIntent.putExtra("deviceOS", os);

                        startActivity(swatActIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Enter Building Name!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsActIntent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsActIntent);
            }
        });


    }

    public File createSessionFile(){
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());

        String filename = date + " - " + buildingEditText.getText().toString();

        File outputFile;
        try {
            Toast.makeText(getApplicationContext(), "Hi" + getStorageDirectory().getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
            outputFile = new File(getStorageDirectory(), filename);
            int n = 2;
            while(outputFile.exists()){
                outputFile = new File(getStorageDirectory(), filename + n);
            }
            outputFile.createNewFile();
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();


            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("The app could not find a valid storage device!")
                    .setTitle("FileNotFoundException")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(DialogInterface dialogInterface) {
                            System.exit(-1);
                        }
                    });
            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }});

            AlertDialog dialog = builder.create();
            dialog.show();
            return null;
        }

        catch (Exception e) {
            e.printStackTrace();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Error creating new file")
                    .setTitle(e.getMessage())
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(DialogInterface dialogInterface) {
                            System.exit(-1);
                        }
                    });

            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }});
            AlertDialog dialog = builder.create();
            dialog.show();
            return null;
        }
        return outputFile;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getStorageDirectory() throws FileNotFoundException{
        // Get the directory for the user's public pictures directory.
        if (isExternalStorageWritable()) {
            File file = new File(getExternalFilesDir(null), "com.alexkdawson.cssdswat");
            return file;
        }else{
            throw new FileNotFoundException();

        }
    }
}
