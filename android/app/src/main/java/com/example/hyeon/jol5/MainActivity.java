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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

class Packet{//TODO set network flow to get

}
class Sender extends Thread{
    MainActivity act;
    public boolean active=true;
    public int nowDelay=0;
    public Queue<Packet> packets=new LinkedList<>();
    public boolean sent=false,flag=false;
    public Sender(MainActivity act){
        this.act=act;
    }
    @Override
    public void run() {
        while(active) {
            try {
                if (nowDelay > 0) {
                    nowDelay -= 50;
                } else if(!sent){
                    flag=true;
                }
                sleep(50);
                if (flag && !sent) {
                    sendToServer();
                    sent = true;
                    flag = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void addPacket(Packet p){
        packets.add(p);
    }
    public void sendToServer(){
        //TODO sendToServer packets
        act.generateToast(act.getString(R.string.update_success));
    }

}
public class MainActivity extends AppCompatActivity {
    SharedPreferences pref;
    public Button refresh,option,tempUp,tempDown,termUp,termDown;
    public EditText tempInput,termInput;
    public TextView nowTemp,nowWater;
    public int maxDelay;
    Sender sender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, TitleActivity.class);
        startActivity(intent);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        //Initializing
        refresh=findViewById(R.id.RefreshButton);
        option=findViewById(R.id.OptionButton);
        tempUp=findViewById(R.id.TempUp);
        tempDown=findViewById(R.id.TempDown);
        termUp=findViewById(R.id.TermUp);
        termDown=findViewById(R.id.TermDown);
        tempInput=findViewById(R.id.TargetTemp);
        termInput=findViewById(R.id.TargetTerm);
        nowTemp=findViewById(R.id.NowTemp);
        nowWater=findViewById(R.id.NowLevel);
        //Event Binding
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshClick();
            }
        });
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClick();
            }
        });
        tempUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempUpClick();
            }
        });
        tempDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempDownClick();
            }
        });
        termUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                termUpClick();
            }
        });
        termDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                termDownClick();
            }
        });
        refreshClick();
    }
    public void onResume() {
        super.onResume();
        pref=getSharedPreferences("EES",MODE_PRIVATE);
        maxDelay=SettingActivity.MaxDelay/100*pref.getInt("autoUpdate",20)+5;
        sender=new Sender(this);
        sender.start();
    }

    public void onPause() {
        sender.active = false;
        super.onPause();
    }

    public void generateToast(final String data){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("Toaster",data);
                Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshClick(){

    }
    public void optionClick(){
        Intent intent=new Intent(this,SettingActivity.class);
        startActivity(intent);
    }
    public void tempUpClick(){
        String tmpStr=tempInput.getText().toString();
        if(tmpStr.length()==0) tmpStr="0";
        double tmp=Double.parseDouble(tmpStr);
        tmp+=.1;
        tempInput.setText(String.format("%.1f",tmp));
        sender.nowDelay=maxDelay;
        sender.sent=false;
    }
    public void tempDownClick(){
        String tmpStr=tempInput.getText().toString();
        if(tmpStr.length()==0) tmpStr="0";
        double tmp=Double.parseDouble(tmpStr);
        tmp-=.1;
        tempInput.setText(String.format("%.1f",tmp));
        sender.nowDelay=maxDelay;
        sender.sent=false;
    }
    public void termUpClick(){
        String tmpStr=termInput.getText().toString();
        if(tmpStr.length()==0) tmpStr="1";
        int tmp=Integer.parseInt(tmpStr);
        tmp+=1;
        termInput.setText(String.format("%d",tmp));
        sender.nowDelay=maxDelay;
        sender.sent=false;
    }
    public void termDownClick(){
        String tmpStr=termInput.getText().toString();
        if(tmpStr.length()==0) tmpStr="1";
        int tmp=Integer.parseInt(tmpStr);
        tmp-=1;
        if(tmp<1)tmp=1;
        termInput.setText(String.format("%d",tmp));
        sender.nowDelay=maxDelay;
        sender.sent=false;
    }


}