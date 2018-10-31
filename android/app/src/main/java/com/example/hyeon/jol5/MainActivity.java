package com.example.hyeon.jol5;

import android.content.Intent;
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
class Packer{

}
class Sender extends Thread{
    MainActivity act;
    public static int clickDelay=800;
    public int nowDelay=4000;
    public boolean sent=false,flag=false;
    public Sender(MainActivity act){
        this.act=act;
    }
    @Override
    public void run() {
        while(true) {
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
    public void sendToServer(){
        act.generateToast(act.getString(R.string.update_success));
    }

}
public class MainActivity extends AppCompatActivity {
    public Button refresh,option,tempUp,tempDown,termUp,termDown;
    public EditText tempInput,termInput;
    public TextView nowTemp,nowWater;

    Sender sender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, TitleActivity.class);
        startActivity(intent);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
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
        sender=new Sender(this);
        sender.start();
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

    }
    public void tempUpClick(){
        String tmpStr=tempInput.getText().toString();
        if(tmpStr.length()==0) tmpStr="0";
        double tmp=Double.parseDouble(tmpStr);
        tmp+=.01;
        tempInput.setText(String.format("%.2f",tmp));
        sender.nowDelay=Sender.clickDelay;
        sender.sent=false;
    }
    public void tempDownClick(){
        String tmpStr=tempInput.getText().toString();
        if(tmpStr.length()==0) tmpStr="0";
        double tmp=Double.parseDouble(tmpStr);
        tmp-=.01;
        tempInput.setText(String.format("%.2f",tmp));
        sender.nowDelay=Sender.clickDelay;
        sender.sent=false;
    }
    public void termUpClick(){

    }
    public void termDownClick(){

    }


}