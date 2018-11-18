package com.example.hyeon.jol5;

public class Packet {//TODO set network flow to get
    static String diff="\n";
    public float nowTemp=0f,nowHum=0f,nowWater=0f,targetTemp=0f;
    public int targetTerm=0;
    public void ParseToPacket(String stream){
        String[] data=stream.split(diff);
        try {
            nowTemp = Float.parseFloat(data[0]);
            nowHum = Float.parseFloat(data[1]);
            nowWater = Float.parseFloat(data[2]);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }
}
