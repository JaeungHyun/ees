package com.example.hyeon.jol5;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

class Sender extends Thread{
    MainActivity act;
    InetSocketAddress ip;
    public boolean active=true;
    public int nowDelay=100;
    public String packet="";
    public boolean sent=false,flag=false;
    public Sender(MainActivity act,String ip,int port){
        this.ip=new InetSocketAddress(ip,port);
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
                sent = true;
                flag = false;
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