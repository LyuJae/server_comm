package com.example.ryu.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Ryu on 2017-11-05.
 */

public class SignupActivity extends AppCompatActivity{

    EditText nameText;
    EditText idText;
    EditText pwText;
    Button submitBtn;
    Button idcheckBtn;
    ServerCommunication serverCommunication = null;
    Handler mainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("result")){
                case ServerCommunication.SIGNUP_SUCCESS:
                    Toast.makeText(getApplicationContext(), "SIGN UP SUCCESS", Toast.LENGTH_SHORT).show();
                    break;
                case ServerCommunication.SIGNUP_FAIL:
                    Toast.makeText(getApplicationContext(), "SIGN UP FAIL", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Server Connect fail", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Sign Up");
        setContentView(R.layout.signup_layout);

        Init();

        nameText = (EditText)findViewById(R.id.name_text);
        idText = (EditText)findViewById(R.id.id_text);
        pwText = (EditText)findViewById(R.id.pw_text);
        submitBtn = (Button)findViewById(R.id.submit_btn);
        idcheckBtn = (Button)findViewById(R.id.checkid_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupProcess ();
            }
        });
        idcheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void Init(){
        serverCommunication = new ServerCommunication("http://165.132.221.64:52273", mainHandler);
    }
    private void SignupProcess(){
        serverCommunication.SetProperty("id", idText.getText().toString());
        serverCommunication.SetProperty("pw", pwText.getText().toString());
        serverCommunication.SetProperty("name", nameText.getText().toString());
        serverCommunication.SendMSG("signup");
    }
}
