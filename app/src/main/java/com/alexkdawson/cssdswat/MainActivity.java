package com.alexkdawson.cssdswat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.FileSystemNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public Button beginSessionButton;
    public EditText buildingEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beginSessionButton = findViewById(R.id.beginSessionButton);
        buildingEditText = findViewById(R.id.buildingEditText);


        beginSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent swatActIntent = new Intent(getBaseContext(), SwatActivity.class);
                File outputFile = createSessionFile();

                swatActIntent.putExtra("buildingName", buildingEditText.getText().toString());
                swatActIntent.putExtra("outputFile", outputFile);
                //TODO swatActIntent.putExtra("username", outputFile);
                //TODO swatActIntent.putExtra("deviceType", outputFile);
                //TODO swatActIntent.putExtra("deviceOS", outputFile);

                startActivity(swatActIntent);
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
            builder.create();
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
            builder.create();
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
