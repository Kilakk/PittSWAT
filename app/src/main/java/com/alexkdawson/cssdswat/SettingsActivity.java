package com.alexkdawson.cssdswat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    EditText usernameEditText;
    EditText typeEditText;
    EditText osEditText;
    EditText macEditText;
    Button savePrefButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        usernameEditText = findViewById(R.id.deviceUsernameEditText);
        typeEditText = findViewById(R.id.deviceTypeEditText);
        osEditText = findViewById(R.id.deviceOSEditText);
        savePrefButton = findViewById(R.id.savePrefButton);
        macEditText = findViewById(R.id.deviceMACEditText);

        savePrefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!usernameEditText.getText().toString().matches("")
                        && !typeEditText.getText().toString().matches("")
                        && !osEditText.getText().toString().matches("")){
                            if(macEditText.getText().toString().matches("^([0-9]|[a-f]){2}:([0-9]|[a-f]){2}:([0-9]|[a-f]){2}:([0-9]|[a-f]){2}:([0-9]|[a-f]){2}:([0-9]|[a-f]){2}$")) {
                                editor.putString("username", usernameEditText.getText().toString());
                                editor.putString("type", typeEditText.getText().toString());
                                editor.putString("os", osEditText.getText().toString());
                                editor.putString("mac", macEditText.getText().toString());
                                editor.commit();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "MAC address is invalid format!", Toast.LENGTH_SHORT).show();
                            }
                }else{
                    Toast.makeText(getApplicationContext(), "Please Enter a Value for each Field!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String username = pref.getString("username", null);
        String type = pref.getString("type", null);
        String os = pref.getString("os", null);
        String mac = pref.getString("mac", null);

        usernameEditText.setText(username);
        typeEditText.setText(type);
        osEditText.setText(os);
        macEditText.setText(mac);

    }
}
