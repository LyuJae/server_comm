package com.example.ryu.myapplication;

import android.content.SharedPreferences;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ryu on 2017-07-25.
 */

public class ServerCommunication {
    private String mURL = null;                            //thingplugURL, https://data.smartcitygoyang.kr:7070/N2M/openAPI/lakepark/monitoring
    private Handler mHandler;
    HttpURLConnection conn = null;
    HashMap<String, String> mProperties = null;

    //Static Values
    final static int SERVER_CONNECT_OK = 0x01;          //Server send correct message
    final static int LOGIN_SUCCESS = 0x02;
    final static int LOGIN_FAIL = 0x03;
    final static int SIGNUP_SUCCESS = 0x05;
    final  static int SIGNUP_FAIL = 0x06;
    final static int SERVER_CONNECT_FAIL = 0x04;

    public ServerCommunication(String URL, Handler handler) {
        this.mURL = URL;
        mHandler = handler;
        mProperties = new HashMap<String, String>();
    }

    public void SetProperty(String key, String value){
        this.mProperties.put(key, value);
    }

    public void SendMSG(final String msg){
        ConnectOpen(msg);
        SetConnectProperty();
        SendMessage();
    }
    private void ConnectOpen(final String msg){
        try {
            URL url = new URL(mURL + "/" + msg); //요청 URL을 입력
            System.out.println("URL : " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
            conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
            conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)
            conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void SetConnectProperty(){
        for(Map.Entry<String, String> entry : mProperties.entrySet()){
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
    private void SendMessage(){
        final Thread thread = new Thread(){
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                try {
                    conn.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append(line + "\n");
                    }
                    System.out.println("sb : " + sb);
                    ReadMessage(sb);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    private void ReadMessage(StringBuilder sb){
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        switch (sb.toString()) {
            case "login_succ\n":
                String cookie = conn.getHeaderFields().get("Set-Cookie").get(0);
                System.out.println("conn.getHeaderFields().get(\"set-cookie\") : " +cookie);
                String session_key = cookie.substring(cookie.indexOf("sk=") + 3, cookie.length());
                System.out.println("Sessionkey : " + session_key);
                bundle.putInt("result", LOGIN_SUCCESS);
                bundle.putString("session_key", session_key);
                msg.setData(bundle);
                break;
            case "login_fail\n":
                bundle.putInt("result", LOGIN_FAIL);
                msg.setData(bundle);
                break;
            case "signup_ok\n":
                bundle.putInt("result", SIGNUP_SUCCESS);
                msg.setData(bundle);
                break;
            default:
                bundle.putInt("result", SERVER_CONNECT_FAIL);
                msg.setData(bundle);
                break;
        }
        mHandler.sendMessage(msg);
    }
}