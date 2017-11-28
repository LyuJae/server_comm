package com.example.ryu.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Ryu on 2017-10-03.
 */

public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    Button signupBtn;
    Button testBtn;
    EditText id_text;
    EditText pw_text;

    Handler mainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SharedPreferences pref = null;
            SharedPreferences.Editor edit = null;
            switch (msg.getData().getInt("result")){
                case ServerCommunication.SERVER_CONNECT_SUCCESS:
                    Toast.makeText(getApplicationContext(), "Login Succ", Toast.LENGTH_SHORT).show();
                    pref = getSharedPreferences("info", MODE_PRIVATE);
                    edit = pref.edit();
                    edit.putString("usr_id", id_text.getText().toString());
                    edit.putString("islogin", "true");
                    edit.putString("uuid", msg.getData().getString("session_key"));
                    edit.commit();
                    finish();
                    break;
                case ServerCommunication.SERVER_CONNECT_FAIL:
                    Toast.makeText(getApplicationContext(), "... Login Fail", Toast.LENGTH_SHORT).show();
                    pref = getSharedPreferences("info", MODE_PRIVATE);
                    edit = pref.edit();
                    edit.putString("usr_id", id_text.getText().toString());
                    edit.putString("islogin", "false");
                    edit.commit();
                    break;
            }
        }
    };
    ServerCommunication mServer = null;
    ServerCommunication testServer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        setTitle("Login");
        Init();

        id_text = (EditText)findViewById(R.id.edittext_id);
        pw_text = (EditText)findViewById(R.id.edittext_pw);
        loginBtn = (Button)findViewById(R.id.loginbtn);
        signupBtn = (Button)findViewById(R.id.signup_btn);
        testBtn = (Button)findViewById(R.id.test_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServer.LoginProcess(id_text.getText().toString(), pw_text.getText().toString());
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void Init(){
        mServer = new ServerCommunication(mainHandler);
    }
}
