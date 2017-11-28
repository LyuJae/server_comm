package com.example.ryu.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Ryu on 2017-11-28.
 */

public class ProjectActivity extends AppCompatActivity {

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

    String project_id = null;
    ServerCommunication mServer = null;
    String usr_id = null;
    Button testBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_layout);
        setTitle("Project_name");

        Init();


        testBtn = (Button)findViewById(R.id.test_btn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServer.GetProjectMember(project_id);
                //mServer.GetProjectPost(project_id);
                //mServer.GetProjectTodo(project_id);
            }
        });

        /*
        LinearLayout ll = new LinearLayout(getApplicationContext());
        ll = (LinearLayout)findViewById(R.id.linear_layout);

        Button tBtn = new Button(getApplicationContext());
        tBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tBtn.setText("Button");
        tBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        ll.addView(tBtn);
        */
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        System.out.println("onPostResume");

    }
    void Init(){
        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        this.project_id = pref.getString("project_id", "");
        System.out.println("project_id : " + project_id);
        mServer = new ServerCommunication(mainHandler);
    }
}
