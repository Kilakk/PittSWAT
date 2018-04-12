package com.alexkdawson.cssdswat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public Button beginSessionButton;
    public Button settingsButton;
    public Button continueSessionButton;
    public EditText buildingEditText;
    public RadioGroup radioGroup;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},4);
        }


        beginSessionButton = findViewById(R.id.beginSessionButton);
        continueSessionButton = findViewById(R.id.continueSessionButton);
        buildingEditText = findViewById(R.id.buildingEditText);
        settingsButton = findViewById(R.id.settings_button_main);
        radioGroup = findViewById(R.id.radioGroup);

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
                    String mac = pref.getString("mac", null);

                    int radioId = radioGroup.getCheckedRadioButtonId();


                    if (username == null || type == null || os == null || mac == null) {
                        Intent settingsActIntent = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(settingsActIntent);
                        Toast.makeText(getApplicationContext(), "Please Enter Information About Device!",
                                Toast.LENGTH_LONG).show();
                    } else if (!buildingEditText.getText().toString().matches("") && radioId != -1) {
                        RadioButton radioButton = radioGroup.findViewById(radioId);
                        Intent swatActIntent = new Intent(getBaseContext(), SwatActivity.class);
                        File outputFile = createSessionFile(radioButton.getText().toString());
                        if(outputFile == null){
                            return;
                        }

                        swatActIntent.putExtra("buildingName", buildingEditText.getText().toString());
                        swatActIntent.putExtra("outputFile", outputFile);
                        swatActIntent.putExtra("username", username);
                        swatActIntent.putExtra("deviceType", type);
                        swatActIntent.putExtra("deviceOS", os);
                        swatActIntent.putExtra("mac", mac);
                        swatActIntent.putExtra("networkToTest", radioButton.getText().toString());

                        startActivity(swatActIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Enter Building Name!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        continueSessionButton.setOnClickListener(new View.OnClickListener() {
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
                    String mac = pref.getString("mac", null);

                    int radioId = radioGroup.getCheckedRadioButtonId();


                    if (username == null || type == null || os == null || mac == null) {
                        Intent settingsActIntent = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(settingsActIntent);
                        Toast.makeText(getApplicationContext(), "Please Enter Information About Device!",
                                Toast.LENGTH_LONG).show();
                    } else {

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("text/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);

                        try {
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select a File to Upload"),
                                    0);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a Dialog
                            Toast.makeText(MainActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0: //FILE SELECT
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("TAG", "File Uri: " + uri.toString());
                    // Get the path
                    String path = "";
                    try {
                        path = getPath(MainActivity.this, uri);
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Error loading file!", Toast.LENGTH_SHORT).show();
                    }





                    Log.d("TAG", "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload

                    File outputFile = new File(path);
                    if(!outputFile.exists()){
                        Toast.makeText(MainActivity.this, "!!" + path + " File does not exist!", Toast.LENGTH_LONG).show();
                        return;
                    }



                    pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    String username = pref.getString("username", null);
                    String type = pref.getString("type", null);
                    String os = pref.getString("os", null);
                    String mac = pref.getString("mac", null);

                    int radioId = radioGroup.getCheckedRadioButtonId();


                    if (username == null || type == null || os == null || mac == null) {
                        Intent settingsActIntent = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(settingsActIntent);
                        Toast.makeText(getApplicationContext(), "Please Enter Information About Device!",
                                Toast.LENGTH_LONG).show();
                    } else {

                        Intent swatActIntent = new Intent(getBaseContext(), SwatActivity.class);

                        swatActIntent.putExtra("outputFile", outputFile);
                        swatActIntent.putExtra("username", username);
                        swatActIntent.putExtra("deviceType", type);
                        swatActIntent.putExtra("deviceOS", os);
                        swatActIntent.putExtra("mac", mac);


                        startActivity(swatActIntent);
                    }

                }
                break;
        }
    }

    public File createSessionFile(String networkToTest){
        String pattern = "yyyy-MM-dd HH_mm_" +
                "ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());

        String filename = date + " - " + buildingEditText.getText().toString()+ " - " + networkToTest + ".csv";

        File outputFile;
        try {
            Toast.makeText(getApplicationContext(), "Hi" + getStorageDirectory(),
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
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "SWAT Files");
            if(!file.exists()){
                file.mkdirs();
            }
            return file;
        }else{
            throw new FileNotFoundException();

        }
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
       return  uri.toString();
    }
}
