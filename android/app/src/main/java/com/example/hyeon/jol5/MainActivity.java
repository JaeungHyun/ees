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
        if(getIntent().getBooleanExtra("flag",true)){
            Intent intent = new Intent(this, TitleActivity.class);
            startActivity(intent);
        }
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
        sender=new Sender(this,pref.getString("IP","127.0.0.1"),pref.getInt("port",4400));
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
        finish();
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