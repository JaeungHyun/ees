package com.example.hyeon.jol5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class SettingActivity extends AppCompatActivity {
    SharedPreferences pref=getSharedPreferences("EES",MODE_PRIVATE);
    SharedPreferences.Editor editor=pref.edit();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

    }
    @Override
    public void onPause() {

        super.onPause();
    }
    @Override
    public void onResume() {

        super.onResume();
    }
}