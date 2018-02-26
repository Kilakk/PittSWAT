package com.alexkdawson.cssdswat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SwatActivity extends AppCompatActivity {

    public String buildingName;
    public File outputFile;
    public String username;
    public String deviceType;
    public String deviceOS;

    public String[] stagingArr;
    ArrayList<String[]> swatList;

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

        swatList = new ArrayList<>();

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

        swatList.add(tempArr);
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

        String[] deepCopy = new String[stagingArr.length];
        for(int i = 0; i < stagingArr.length; i++){
            deepCopy[i] = new String(stagingArr[i]);
        }

        swatList.add(deepCopy);


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, false)); //really inefficient but allows for easier modification i guess :|
            for(int i = 0; i < swatList.size(); i++) {
                for(int j = 0; j < swatList.get(i).length; j++){
                    if(j != 11){
                        writer.append("\"" + swatList.get(i)[j] + "\",");
                    }else{
                        writer.append("\"" + swatList.get(i)[j] + "\"\n");
                    }
                }
            }
            writer.close();
            Toast.makeText(getApplicationContext(), "wrote to:" + outputFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(SwatActivity.this);
            builder.setMessage("The app could not write SWAT event to file")
                    .setTitle("IOException")
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
        }
    }

    public String getDate(){
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    public String getTime(){
        String pattern = "hh:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String time = simpleDateFormat.format(new Date());
        return time;
    }

    public String getDeviceMac(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        return macAddress;
    }

    public String getSignalStrength(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String sigStr = Integer.toString(wInfo.getRssi());
        return sigStr;
    }

    public String getApMac(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String bssid = Integer.toString(wInfo.getRssi());
        return bssid;
    }

    public String getNonPittSsid(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> wifiScan = wifiManager.getScanResults();
        ArrayList<String> nonPitt = new ArrayList<>();

        for(ScanResult s : wifiScan){
            if(s.level > -80){
                nonPitt.add(s.SSID);
            }
        }

        String out = "";
        for(int i = 0; i < nonPitt.size(); i++){
            if(i == 0){
                out += nonPitt.get(i);
            }else{
                out += "," + nonPitt.get(i);
            }
        }
        return out;
    }
}
