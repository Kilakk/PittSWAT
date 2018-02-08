package com.alexkdawson.cssdswat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class SwatActivity extends AppCompatActivity {

    public String buildingName;
    public File outputFile;
    public String username;
    public String deviceType;
    public String deviceOS;

    public String[] stagingArr;
    public String[] titleArr;

    public Button doASwatButton;
    public EditText locationEditText;
    public EditText uploadEditText;
    public EditText downloadEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swat);

        buildingName = (String)getIntent().getExtras().get("buildingName");
        outputFile = (File)getIntent().getExtras().get("outputFile");
        username = (String)getIntent().getExtras().get("username");
        deviceType = (String)getIntent().getExtras().get("deviceType");
        deviceOS = (String)getIntent().getExtras().get("deviceOS");

        doASwatButton = findViewById(R.id.doASwatButton);
        locationEditText = findViewById(R.id.locationEditText);
        uploadEditText = findViewById(R.id.uploadEditText);
        downloadEditText = findViewById(R.id.downloadEditText);

        ArrayList<String[]> swatList = new ArrayList<>();

        String[] tempArr = {
                    "\"date\"",
                    "\"time\"",
                    "\"username\"",
                    "\"location description\"",
                    "\"device type\"",
                    "\"device os\"",
                    "\"MAC address\"",
                    "\"Download (Mbps)\"",
                    "\"Upload (Mbps)\"",
                    "\"signal strength (dBm)\"",
                    "\"AP MAC address\"",
                    "\"non-pitt SSID with signal stronger than -80\""};
        titleArr = tempArr;

        stagingArr = new String[12];

        doASwatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullSwatInfo();
            }
        });

    }

    public void pullSwatInfo(){
        stagingArr[0]   = getDate();
        stagingArr[1]   = getTime();
        stagingArr[2]   = username;
        stagingArr[3]   = locationEditText.getText().toString();
        stagingArr[4]   = deviceType;
        stagingArr[5]   = deviceOS;
        stagingArr[6]   = getDeviceMac();
        stagingArr[7]   = downloadEditText.getText().toString();
        stagingArr[8]   = uploadEditText.getText().toString();
        stagingArr[9]   = getSignalStrength();
        stagingArr[10]  = getApMac();
        stagingArr[11]  = getNonPittSsid();

        //TODO copy staging to swatList
        //TODO write update swatList to file
    }

    //TODO implement
    public String getDate(){}
    public String getTime(){}
    public String getDeviceMac(){}
    public String getSignalStrength(){}
    public String getApMac(){}
    public String getNonPittSsid(){}

}
