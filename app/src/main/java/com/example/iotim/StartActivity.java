package com.example.iotim;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_start.xml 화면 출력
        setContentView(R.layout.activity_start);

        //저장된 ip, port 가지고 오기.
        SharedPreferences auto = getSharedPreferences("autoConnect", Activity.MODE_PRIVATE);
        String cIP = auto.getString("ip", null);
        String cPort = auto.getString("port", null);

        //화면의 입력칸 객체
        EditText ip = (EditText)findViewById(R.id.ip);
        EditText port = (EditText)findViewById(R.id.port);

        //저장된 ip, port를 입력칸에 입력. 저장된 값이 없다면 넘어감.
        if(cIP != null && cPort != null){
           ip.setText(cIP);
           if(cPort.substring(0,1).equals("."))
               port.setText(cPort.substring(1));
           else
               port.setText(cPort);
        }

        //화면의 버튼 객체
        Button button = (Button)findViewById(R.id.button);
        //버튼 클릭시 동작
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력칸의 값을 String으로 받아옴.
                String connectIP = ip.getText().toString();
                String connectPort = port.getText().toString();

                //받아온 ip, port값을 저장
                SharedPreferences auto = getSharedPreferences("autoConnect", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoConnectEdit = auto.edit();
                autoConnectEdit.putString("ip", connectIP);
                autoConnectEdit.putString("port", "." + connectPort);
                autoConnectEdit.commit();

                //ip, port값을 MainActivity에 넘기며 MainActivity 실행
                Intent in = new Intent(StartActivity.this, MainActivity.class);
                in.putExtra("ip", connectIP);
                in.putExtra("port", Integer.parseInt(connectPort));
                startActivity(in);
            }
        });


    }

}
