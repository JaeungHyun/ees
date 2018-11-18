package com.example.hyeon.jol5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Packet{//TODO set network flow to get
    static String diff=",";
    public float nowTemp=0f,nowHum=0f,nowWater=0f,targetTemp=0f;
    public int targetTerm=0,nowTerm=0;
    public void ParseToPacket(String stream){
        String[] data=stream.split(diff);
        try {
            nowTemp = Float.parseFloat(data[0]);
            nowHum = Float.parseFloat(data[1]);
            nowTerm = Integer.parseInt(data[2]);
            nowWater = Float.parseFloat(data[3]);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }
}
class Sender extends Thread{
    MainActivity act;
    InetSocketAddress ip=new InetSocketAddress("10.149.154.31",12222);//TODO set proper address
    public boolean active=true;
    public int nowDelay=0;
    public String packet="";
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
                    act.generateToast(act.getString(R.string.update_success));
                }
            } catch (Exception e) {
                e.printStackTrace();
                act.generateToast(act.getString(R.string.update_failed));
            }
        }
    }
    public void addPacket(String p){
        packet=p;
    }
    public String sendToServer() throws IOException {
        String ret="";
        while(packet.length()!=0){
            String stream=packet;
            Socket sock=new Socket();
            sock.setSoTimeout(2);
            sock.connect(ip);
            BufferedReader in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
            OutputStream out=sock.getOutputStream();
            out.write(stream.getBytes());
            out.flush();
            String buf=in.readLine();
            while(!buf.equals("")){
                ret+=buf+"\n";
                buf=in.readLine();
            }
            out.close();
            in.close();
            sock.close();
        }
        act.pack.ParseToPacket(ret);
        act.nowTemp.setText(""+act.pack.nowTemp);
        act.nowWater.setText(""+act.pack.nowWater);
        act.nowHum.setText(""+act.pack.nowHum);
        Log.d("input",ret);
        return ret;
    }
}

public class MainActivity extends AppCompatActivity {
    SharedPreferences pref;
    public Button refresh,option,tempUp,tempDown,termUp,termDown;
    public EditText tempInput,termInput;
    public TextView nowTemp,nowWater,nowHum;
    public int maxDelay;
    Sender sender;
    Packet pack=new Packet();
    class EditAction implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch(v.getId()){
                case R.id.TargetTemp:
                    pack.targetTemp=Float.parseFloat(tempInput.getText().toString());
                    break;
                case R.id.TargetTerm:
                    pack.targetTerm=Integer.parseInt(termInput.getText().toString());
                    break;
            }
            return false;
        }
    }
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
        nowHum=findViewById(R.id.NowHum);
        //Event Binding
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOption();
            }
        });
        tempUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempUp();
            }
        });
        tempDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempDown();
            }
        });
        termUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                termUp();
            }
        });
        termDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                termDown();
            }
        });
        tempInput.setOnEditorActionListener(new EditAction());
        termInput.setOnEditorActionListener(new EditAction());

    }
    public void onResume() {
        super.onResume();
        pref=getSharedPreferences("EES",MODE_PRIVATE);
        maxDelay=SettingActivity.MaxDelay/100*pref.getInt("autoUpdate",20)+5;
        sender=new Sender(this);
        sender.start();
        refresh();
        pack.targetTemp=pref.getFloat("temp",20f);
        pack.targetTerm=pref.getInt("term",24);
        setTempView();
        setTermView();
    }

    public void onPause() {
        sender.active = false;
        SharedPreferences.Editor editor=pref.edit();
        editor.putFloat("temp",pack.targetTemp);
        editor.putInt("term",pack.targetTerm);
        editor.commit();
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

    public void refresh(){
        triggerSender();

    }
    public void toOption(){
        Intent intent=new Intent(this,SettingActivity.class);
        startActivity(intent);
    }
    public void setTempView(){
        String tmpStr=String.format("%.1f",pack.targetTemp);
        tempInput.setText(tmpStr);
    }
    public void setTermView(){
        String tmpStr=String.format("%d",pack.targetTerm);
        termInput.setText(tmpStr);
    }
    public void tempUp(){
        pack.targetTemp+=.1;
        setTempView();
        triggerSender();
    }
    public void tempDown(){
        pack.targetTemp-=.1;
        setTempView();
        triggerSender();
    }
    public void termUp(){
        pack.targetTerm+=1;
        setTermView();
        triggerSender();
    }
    public void termDown(){
        if(pack.targetTerm>1)pack.targetTerm-=1;
        setTermView();
        triggerSender();
    }
    public void triggerSender(){
        sender.addPacket(String.format("%.1f",pack.targetTemp)+","+(pack.targetTerm*3600));
        sender.nowDelay=maxDelay;
        sender.sent=false;
    }

}