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
                    boolean test=sendToServer();
                    sent = true;
                    flag = false;
                    if(test)
                    act.generateToast(act.getString(R.string.update_success));
                    else
                        act.generateToast(act.getString(R.string.update_failed));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void addPacket(String p){
        packet=p;
    }
    public boolean sendToServer(){
        String ret="";
        //while(packet.length()!=0){
            String stream=packet;
            Socket sock=new Socket();
            try {
                sock.connect(ip,1000);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                OutputStream out = sock.getOutputStream();
                out.write(stream.getBytes());
                out.flush();
                String buf = in.readLine();
                while (buf!=null) {
                    ret += buf + "\n";
                    buf = in.readLine();
                }
                in.close();
                out.close();
                sock.close();
                act.pack.ParseToPacket(ret);
            }catch(IOException e){
                e.printStackTrace();
                return false;
            }
        //}
        act.nowTemp.setText(String.format("%.1f",act.pack.nowTemp));
        if(act.pack.nowWater==1)
            act.nowWater.setText(act.getString(R.string.enough_water));
        else
            act.nowWater.setText(act.getString(R.string.lack_water));
        act.nowHum.setText(String.format("%.1f",act.pack.nowHum));
        Log.d("input",ret);
        return true;
    }
}
