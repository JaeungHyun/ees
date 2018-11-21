package com.example.hyeon.jol5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class SettingActivity extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SeekBar autoUpdate;
    TextView autoUpdateView;
    EditText ipIn,portIn;
    public static final int MaxDelay=5000;
    int nowDelay=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        autoUpdate=findViewById(R.id.AutoUpdateTermSeekBar);
        autoUpdateView=findViewById(R.id.AutoUpdateTermValue);
        ipIn=findViewById(R.id.IpInput);
        portIn=findViewById(R.id.PortInput);
        autoUpdate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int i, boolean b) {moveSeekBar(i);}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    @Override
    public void onPause() {
        editor.putInt("autoUpdate",nowDelay);
        editor.putInt("port",Integer.parseInt(portIn.getText().toString()));
        editor.putString("IP",ipIn.getText().toString());
        editor.commit();
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        pref=getSharedPreferences("EES",MODE_PRIVATE);
        editor=pref.edit();
        nowDelay=pref.getInt("autoUpdate",20);
        ipIn.setText(pref.getString("IP","127.0.0.1"));
        portIn.setText(String.valueOf(pref.getInt("port",4400)));
        autoUpdate.setProgress(nowDelay);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("flag",false);
        startActivity(intent);
        super.onBackPressed();
    }
    public void moveSeekBar(int i) {
        nowDelay = i;
        autoUpdateView.setText(String.format("%.02f", MaxDelay / 100f * nowDelay / 1000 + 0.05f));
    }

}