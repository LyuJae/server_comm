package com.example.ryu.myapplication;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button loginBtn, projectBtn;


    String usr_id = null;
    String isLogin = "false";
    ServerCommunication mServer = null;


    Handler mainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("result")){
                case ServerCommunication.SERVER_CONNECT_SUCCESS:
                    Toast.makeText(getApplicationContext(), "Connect Succ", Toast.LENGTH_SHORT).show();
                    System.out.println("list : " + msg.getData().getString("list"));
                    break;
                case ServerCommunication.SERVER_CONNECT_FAIL:
                    Toast.makeText(getApplicationContext(), "Connect Fail", Toast.LENGTH_SHORT).show();


                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate Call");
        super.onCreate(savedInstanceState);

        Init();

        setContentView(R.layout.activity_main);
        loginBtn = (Button)findViewById(R.id.btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                //Intent intent = new Intent(getApplicationContext(), LoadingDialog.class);
                startActivity(intent);
            }
        });
        projectBtn = (Button)findViewById(R.id.project_btn);
        projectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = null;
                SharedPreferences.Editor edit = null;
                pref = getSharedPreferences("info", MODE_PRIVATE);
                edit = pref.edit();
                edit.putString("project_id", "8888");
                edit.commit();
                Intent intent = new Intent(getApplicationContext(), ProjectActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        System.out.println("onResume Call");
        super.onResume();
        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        this.usr_id = pref.getString("usr_id", "");

        this.isLogin = pref.getString("islogin", "false");
        System.out.println("usr_id : " + this.usr_id);
        System.out.println("isLogin : " + this.isLogin);

        if(this.isLogin.compareTo("true") == 0){
            //탭이 프로젝트/일정에 따라
            mServer.GetMyTodoProcess(this.usr_id);
            //list : {"project_name":"pro8","start_date":"201711050000","due_date":"201711280000","title":"ppt준비","content":"발표자료 준비"}
            //mServer.GetMyProjectProcess(this.usr_id);
            //{"project_id":2,"project_name":null,"status":"yes"},{"project_id":8888,"project_name":"pro8","status":"yes"},{"project_id":7777,"project_name":"pro7","status":"yes"},{"project_id":9999,"project_name":"myname","status":"wait"}

        }
    }

    void Init(){
        SharedPreferences pref = null;
        SharedPreferences.Editor edit = null;
        pref = getSharedPreferences("info", MODE_PRIVATE);
        edit = pref.edit();
        edit.putString("usr_id", "");
        edit.putString("islogin", "false");
        edit.putString("project_id", "");
        edit.commit();

        mServer = new ServerCommunication(mainHandler);
    }

}
