package com.alexkdawson.cssdswat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Browser;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
    public String deviceMac;

    public String[] stagingArr;
    ArrayList<String[]> swatList;
    ArrayList<String> swatListString;
    ArrayAdapter<String> arrayAdapter;

    public Button doASwatButton;
    public Button speedTestButton;
    public Button emailReadingButton;
    public EditText locationEditText;
    public EditText uploadEditText;
    public EditText downloadEditText;
    public ListView swatListView;
    public TextView readingsDoneCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swat);

        buildingName = (String)getIntent().getExtras().get("buildingName");
        outputFile = (File)getIntent().getExtras().get("outputFile");
        username = (String)getIntent().getExtras().get("username");
        deviceType = (String)getIntent().getExtras().get("deviceType");
        deviceOS = (String)getIntent().getExtras().get("deviceOS");
        deviceMac = (String)getIntent().getExtras().get("mac");

        doASwatButton = findViewById(R.id.doASwatButton);
        locationEditText = findViewById(R.id.locationEditText);
        uploadEditText = findViewById(R.id.uploadEditText);
        downloadEditText = findViewById(R.id.downloadEditText);
        swatListView = findViewById(R.id.swatListView);
        speedTestButton = findViewById(R.id.speedTestButton);
        readingsDoneCount = findViewById(R.id.readings_done_count);
        emailReadingButton = findViewById(R.id.email_readings_btn);

        swatList = new ArrayList<>();
        swatListString = new ArrayList<>();

        String[] tempArr = {
                    "date",
                    "time",
                    "username",
                    "location description",
                    "device type",
                    "device os",
                    "MAC address",
                    "Download (Mbps)",
                    "Upload (Mbps)",
                    "signal strength (dBm)",
                    "AP MAC address",
                    "non-pitt SSID with signal stronger than -80"};

        swatList.add(tempArr);
        stagingArr = new String[12];

        doASwatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFields()) {
                    pullSwatInfo();
                    uploadEditText.setText("");
                    downloadEditText.setText("");
                    locationEditText.setText("");
                }else{
                    Toast.makeText(SwatActivity.this, "Please fill in all fields before doing a swat!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        speedTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com/search?q=speed+test";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.putExtra(Browser.EXTRA_APPLICATION_ID, SwatActivity.this.getPackageName());
                startActivity(i);
            }
        });

        emailReadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri path =FileProvider.getUriForFile(SwatActivity.this, "com.alexkdawson.fileprovider", outputFile);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent .setType("vnd.android.cursor.dir/email");
                String to[] = {"hlc47@pitt.edu", "kateulreich@pitt.edu", "djm201@pitt.edu"};
                emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
                emailIntent .putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Swat for " + outputFile.getName());
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(emailIntent , "Send email..."));
            }
        });

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, swatListString);
        swatListView.setAdapter(arrayAdapter);

        swatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // custom dialog
                final Dialog dialog = new Dialog(SwatActivity.this);
                dialog.setContentView(R.layout.swat_dialog);
                dialog.setTitle("SWAT #" + id);

                final int idInt = (new Long(id).intValue());
                String[] currSwat = swatList.get(idInt + 1);

                // set the custom dialog components - text, image and button
                TextView diagDateTime = dialog.findViewById(R.id.diag_date_time);
                TextView diagUsername = dialog.findViewById(R.id.diag_username);
                TextView diagLocation = dialog.findViewById(R.id.diag_location);
                TextView diagTypeOs = dialog.findViewById(R.id.diag_type_os);
                TextView diagDevMac = dialog.findViewById(R.id.diag_dev_mac);
                TextView diagDownUp = dialog.findViewById(R.id.diag_down_up);
                TextView diagSignalStr = dialog.findViewById(R.id.diag_signal_str);
                TextView diagApMac = dialog.findViewById(R.id.diag_ap_mac);
                TextView diagNonPitt = dialog.findViewById(R.id.diag_non_pitt);

                diagDateTime.setText(currSwat[1] + " " + currSwat[0]);
                diagUsername.setText(currSwat[2]);
                diagLocation.setText(currSwat[3]);
                diagTypeOs.setText(currSwat[4] + "/" + currSwat[5]);
                diagDevMac.setText(currSwat[6]);
                diagDownUp.setText(currSwat[7] + "/" + currSwat[8]);
                diagSignalStr.setText(currSwat[9]);
                diagApMac.setText(currSwat[10]);
                diagNonPitt.setText(currSwat[11]);

                Button dialogButton = (Button) dialog.findViewById(R.id.diag_delete_button);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SwatActivity.this);

                        builder.setTitle("You are about to delete a SWAT reading!");
                        builder.setMessage("Are you sure?");

                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface subDialog, int which) {
                                subDialog.dismiss();
                                dialog.dismiss();
                                deleteReading(idInt);
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface subDialog, int which) {
                                subDialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

                dialog.show();



                Toast.makeText(getBaseContext(),swatList.get(position + 1)[0],Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void pullSwatInfo(){
        stagingArr[0]   = getDate();
        stagingArr[1]   = getTime();
        stagingArr[2]   = username;
        stagingArr[3]   = buildingName + " - " + locationEditText.getText().toString();
        stagingArr[4]   = deviceType;
        stagingArr[5]   = deviceOS;
        stagingArr[6]   = deviceMac;
        stagingArr[7]   = downloadEditText.getText().toString();
        stagingArr[8]   = uploadEditText.getText().toString();
        stagingArr[9]   = getSignalStrength();
        stagingArr[10]  = getApMac();
        stagingArr[11]  = getNonPittSsid();

        String[] deepCopy = new String[stagingArr.length];
        String deepCopyString = "";
        for(int i = 0; i < stagingArr.length; i++){
            deepCopy[i] = new String(stagingArr[i]);
            deepCopyString += stagingArr[i];
            if(i != stagingArr.length - 1){
                deepCopyString += ", ";
            }
        }

        swatList.add(deepCopy);
        swatListString.add(deepCopyString);
        arrayAdapter.notifyDataSetChanged();

        saveToFile();
        readingsDoneCount.setText(swatListString.size() + "");
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

    public String getSignalStrength(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String sigStr = Integer.toString(wInfo.getRssi());
        return sigStr;
    }

    public String getApMac(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String bssid = wInfo.getBSSID();
        if(bssid == null || bssid.isEmpty()){
            return "null";
        }
        return bssid;
    }

    public String getNonPittSsid() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> wifiScan = wifiManager.getScanResults();


        ArrayList<String> nonPitt = new ArrayList<>();


        for (ScanResult s : wifiScan) {
            if (s.level > -80 && !s.SSID.equals("WIRELESS-PITTNET-FAST") && !s.SSID.equals("WIRELESS-PITTNET") && !s.SSID.equals("eduroam")) {
            nonPitt.add(s.SSID);
            }
        }
        String out = "";
        boolean first = true;
        for (int i = 0; i < nonPitt.size(); i++) {
            if (!nonPitt.get(i).equals("")) {
                if (first) {
                    first = false;
                    out += nonPitt.get(i);
                } else {
                    out += "," + nonPitt.get(i);
                }
            }
        }
        return "[" + out + "]";
    }

    public void deleteReading(int id){
        swatList.remove(id + 1);
        swatListString.remove(id);
        arrayAdapter.notifyDataSetChanged();
        Toast.makeText(getBaseContext(),"Reading Deleted!",Toast.LENGTH_SHORT).show();
        saveToFile();
        readingsDoneCount.setText(swatListString.size() + "");
    }

    public void saveToFile(){
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

    public boolean validateFields(){
        if(downloadEditText.getText().toString().matches("") || uploadEditText.getText().toString().matches("") || locationEditText.getText().toString().matches("")){
            return false;
        }
        return true;
    }
}
