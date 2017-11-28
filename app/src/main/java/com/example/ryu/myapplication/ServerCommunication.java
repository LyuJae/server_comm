package com.example.ryu.myapplication;

import android.content.SharedPreferences;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ryu on 2017-07-25.
 */

public class ServerCommunication {
    private String mURL = "http://165.132.221.64:52273";                            //thingplugURL, https://data.smartcitygoyang.kr:7070/N2M/openAPI/lakepark/monitoring
    private Handler mHandler;



    //Static Values
    final static int SERVER_CONNECT_SUCCESS = 0x01;          //Server send correct message
    final static int LOGIN_SUCCESS = 0x02;
    final static int LOGIN_FAIL = 0x03;
    final static int SIGNUP_SUCCESS = 0x05;
    final  static int SIGNUP_FAIL = 0x06;
    final static int SERVER_CONNECT_FAIL = 0x04;
    public ServerCommunication(Handler handler) {
        mHandler = handler;
    }

    public void LoginProcess(String id_text, String pw_text){
        HashMap<String, String> mProperties = new HashMap<String, String>();
        this.SetProperty(mProperties, "id", id_text);
        this.SetProperty(mProperties, "pw", pw_text);
        this.SendMSG(mProperties,"login");
    }
    public void PostProcess(){
        HashMap<String, String> mProperties = new HashMap<String, String>();
        this.SetProperty(mProperties, "Accept", "application/json");
        this.SetProperty(mProperties, "Content-type", "application/json");
        this.SendMSG(mProperties,"post");
    }
    public void GetMyTodoProcess(String usr_id){
        HashMap<String, String> mProperties = new HashMap<String, String>();
        this.SetProperty(mProperties, "usr_id", usr_id);
        this.SendMSG(mProperties,"get_my_todo");
    }
    public void GetMyProjectProcess(String usr_id){
        HashMap<String, String> mProperties = new HashMap<String, String>();
        this.SetProperty(mProperties, "usr_id", usr_id);
        this.SendMSG(mProperties,"my_project");
    }
    public void GetProjectMember(String project_id){
        HashMap<String, String> mProperties = new HashMap<String, String>();
        this.SetProperty(mProperties, "project_id", project_id);
        this.SendMSG(mProperties,"get_project_member");
    }
    public void GetProjectTodo(String project_id){
        HashMap<String, String> mProperties = new HashMap<String, String>();
        this.SetProperty(mProperties, "project_id", project_id);
        this.SendMSG(mProperties,"get_project_todo");
    }
    public void GetProjectPost(String project_id){
        HashMap<String, String> mProperties = new HashMap<String, String>();
        this.SetProperty(mProperties, "project_id", project_id);
        this.SendMSG(mProperties, "get_project_post");
    }

    public void SetProperty(HashMap<String, String> mProperties, String key, String value){
        mProperties.put(key, value);
    }

    public void SendMSG(HashMap<String, String> mProperties, final String msg){
        HttpURLConnection conn = null;

        URL url = null; //요청 URL을 입력
        try {
            url = new URL(mURL + "/" + msg);
            System.out.println("URL : " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConnectOpen(conn, msg);
        SetConnectProperty(conn, mProperties);
        SendMessage(conn);
    }

    private void ConnectOpen(HttpURLConnection conn, final String msg){
        try {

            conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
            conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
            conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)
            conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void SetConnectProperty(HttpURLConnection conn, HashMap<String, String> mProperties){
        for(Map.Entry<String, String> entry : mProperties.entrySet()){
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
    private void SendMessage(final HttpURLConnection conn){
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
    private void SendMessage(final HttpURLConnection conn, final String post){
        final Thread thread = new Thread(){
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                try {
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    dos.write(post.getBytes());
                    dos.flush();
                    dos.close();

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
        String result = sb.substring(sb.indexOf("\"result\"") + 10, sb.indexOf("\"result\"") + 14);
        System.out.println("result : " + result);
        String list = null;
        String err = null;
        if(sb.indexOf("list") != -1)
            list = sb.substring(sb.indexOf("[") + 1, sb.indexOf("]"));

        switch (result) {
            case "succ":
                bundle.putInt("result", SERVER_CONNECT_SUCCESS);
                bundle.putString("list", list);
                msg.setData(bundle);
                break;
            case "fail":
                bundle.putInt("result", SERVER_CONNECT_FAIL);
                msg.setData(bundle);
                break;
            case "login_succ\n":

                /*String cookie = conn.getHeaderFields().get("Set-Cookie").get(0);
                System.out.println("conn.getHeaderFields().get(\"set-cookie\") : " +cookie);
                String session_key = cookie.substring(cookie.indexOf("sk=") + 3, cookie.length());
                System.out.println("Sessionkey : " + session_key);
                bundle.putInt("result", LOGIN_SUCCESS);
                bundle.putString("session_key", session_key);
                msg.setData(bundle);*/
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