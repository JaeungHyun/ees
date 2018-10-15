package com.example.hyeon.jol5;

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


public class MainActivity extends AppCompatActivity {
    private Button mButton;
    private int mNumber=25;
    private ClientThread mClientThread;
    private EditText mEditIP;
    private Button MBC, MBS;  //connec  send
    private TextView Toutput;
    private TextView mEditData;
    private TextView mNowTem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        mEditIP = (EditText)findViewById(R.id.editIP);
        mEditData=(TextView) findViewById(R.id.editData);
        mNowTem=(TextView)findViewById(R.id.nowtem);
        MBC=(Button)findViewById(R.id.btconnect);
        MBS=(Button)findViewById(R.id.btsend);
        Toutput=(TextView)findViewById(R.id.textOut);

        if(savedInstanceState!=null){
            mNumber=savedInstanceState.getInt("number",0);
        }
        mEditData.setText(mNumber + "");


        mButton=(Button)findViewById(R.id.up);
        mButton.setOnClickListener(new MyOnClickListener());

        mButton=(Button)findViewById(R.id.down);  //버튼에는 각각의 Id 가 있어야한다. 버튼 id와 행동을 연계함
        mButton.setOnClickListener(new MyOnClickListener());
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("number",mNumber);
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.up :
                    mNumber++;
                    mEditData.setText(mNumber + "");
                    if(mNumber>30){
                        mNumber--;
                        mEditData.setText(mNumber + "");
                    }
                    break;
                case R.id.down :
                    mNumber--;
                    mEditData.setText(mNumber + "");
                    if(mNumber<20){
                        mNumber++;
                        mEditData.setText(mNumber + "");   //온도 제한 설정 완료.
                    }
                    break;
            }
        }
    }


    public void mOnclick(View v){
        switch (v.getId()){
            case R.id.btconnect :
                if (mClientThread ==null){
                    String str=mEditIP.getText().toString();
                    if(str.length()!=0){
                        mClientThread = new ClientThread(str ,mMainHandler);
                        mClientThread.start();
                        MBC.setEnabled(false);
                        MBS.setEnabled(true);
                    }
                }break;
            case R.id.btquit : finish(); break;
            case R.id.update :



                    break;  //이걸 받아오는 recv로 .
            case R.id.btsend :
                if(SendThread.mHandler !=null){
                    Message msg= Message.obtain();
                    msg.what = 1;
                    msg.obj=mEditData.getText().toString();
                    SendThread.mHandler.sendMessage(msg);
                    mEditData.setText(mNumber+"");
                    mNowTem.setText(mNumber+"");
                } break;
        }
    }

   private Handler mMainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1: //화면에 데이터 출력
                    Toutput.append((String)msg.obj);
                    break;
            }

        }

    };


}

class ClientThread extends Thread{
    private String mServer;
    private Handler mMainH;

    public ClientThread(String server, Handler MainH){
        mServer=server;
        mMainH=MainH;                                }



    @Override
    public  void run(){
        Socket sock=null;
        try{
            sock=new Socket(mServer , 9000);
            doPrintln(">>서버와 연결 성공!");
            SendThread sendThread = new SendThread(this, sock.getOutputStream());
            RecvThread recvThread = new RecvThread(this, sock.getInputStream());
            sendThread.start();
            recvThread.start();    //서버랑 연결하면 보내기-send   받기-recv 스레드 생성
            sendThread.join();
            recvThread.join();
        }catch(Exception e){
            doPrintln(e.getMessage());
        }finally {
            try{
                if(sock != null){
                    sock.close();
                    doPrintln("연결 종료");
                }
            }catch(Exception e){
                doPrintln(e.getMessage());
            }
        }
    }
    public void doPrintln(String str){
        Message msg=Message.obtain();
        msg.what=1;
        msg.obj= str+ "\n";
        mMainH.sendMessage(msg);
    }
}
class SendThread extends Thread{
    private ClientThread mClientThread;
    private OutputStream mOutStream;
    public static Handler mHandler;
    public SendThread(ClientThread clientThread, OutputStream outputStream){
        mClientThread=clientThread;
        mOutStream=outputStream;
    }

    @Override
    public void run(){
        Looper.prepare();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 1: // 데이터 송신
                        try{
                            String s=(String) msg.obj;
                            mOutStream.write(s.getBytes());
                            mClientThread.doPrintln("[ 보낸 데이터 ]" + s);
                        }catch(Exception e){
                            mClientThread.doPrintln(e.getMessage());
                        }                 break;
                    case 2: //스레드 종료
                        getLooper().quit();
                        break;
                }
            }
        }; Looper.loop();
    }
}


class RecvThread extends Thread{
    private ClientThread mClientThread;
    private InputStream mInStream;
    public RecvThread(ClientThread clientThread, InputStream inStream){
        mClientThread = clientThread;
        mInStream=inStream; }

    @Override
    public void run(){
        byte[] bf=new byte[1024];
        while (true){
            try{
                int nbytes= mInStream.read(bf);
                if(nbytes > 0){
                    String s=new String(bf,0,nbytes);
                    mClientThread.doPrintln("[ 받은 데이터 ]" + s);
                }
                else{
                    mClientThread.doPrintln("서버가 연결 끊음");
                    if(SendThread.mHandler != null){
                        Message msg= Message.obtain();
                        msg.what =2;
                        SendThread.mHandler.sendMessage(msg);
                    } break;
                }
            }catch(Exception e){
                mClientThread.doPrintln(e.getMessage());
            }
        } }
}

