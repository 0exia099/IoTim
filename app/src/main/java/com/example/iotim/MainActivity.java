package com.example.iotim;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.iotim.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static Context context_main; //다른 fragment에서 MainActivity의 변수및 메소드에 접근하기위한 번수
    public static Context context;// MainActivity의 context를 가지는 변수
    public InputStream input;   //소켓통신에서 input
    public OutputStream output; //소켓통신에서 output
    public Socket socket;   //소켓
    public Boolean connect = true;  //소켓연결 성공시 true, 실패시 false으로 설정하는 변수
    Thread thread;  //소켓통신을 실행시킬 쓰레드
    public String ip;   //ip변수
    public int port;    //port변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context_main = this;
        context = MainActivity.this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //StartActivity에서 넘긴 ip, port받아옴
        Intent intent = getIntent();
        ip = intent.getExtras().getString("ip");
        port = intent.getExtras().getInt("port");

        Log.d("app", "앱 시작");//동작 확인을 위한 로그
        thread = new Thread(new Runnable() {    //쓰레드로 소켓통신 실행.
            @Override
            public void run() {
                try {
                    int SDK_INT = Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }
                    Log.d("app", "소켓 시작");
                    try {
                        socket = new Socket();  //소켓생성
                        socket.connect(new InetSocketAddress(ip, port), 1000);  //ip, port 설정, 연결 시간 1초 설정
                        Thread.sleep(1000); //1초 기다림
                    } catch (Exception exception) { //연결실패 시 오류처리.
                        MainActivity.this.runOnUiThread(new Runnable() {    //쓰레드 안에서 Toast출력
                            public void run() {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = null; //만들어둔 레이아웃으로 연결 실패 메시지 Toast로 출력하기 위한 View객체
                                layout = inflater.inflate(R.layout.toast_border_fail, (ViewGroup) findViewById(R.id.toast_layout_root));
                                TextView text = (TextView) layout.findViewById(R.id.toast);//레이아웃 설정
                                text.setText("Connection Failed");  //연결 실패 메시지 설정.
                                Toast toast = new Toast(context);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 300);   //Toast위치 설정
                                toast.setView(layout);
                                toast.show();   //Toast 출력
                            }
                        });
                        connect = false;    //연결 실패 알리는 변수
                        socket.close();     //소켓 닫음
                        finish();           //현재 화면 닫음
                    }
                    input = socket.getInputStream();    //연결 성공시 input, output가져옴.
                    output = socket.getOutputStream();
                    Log.d("app", "소켓 연결");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                byte[] buffer = new byte[1024];
                int bytes;
                Log.d("app", "수신 시작");
                while(connect){     //연결 성공 시 서버로부터 계속해서 메시지 받음.
                    try {
                        Log.d("app", "수신 대기");
                        bytes = input.read(buffer); //메시지 받아옴.
                        Log.d("app", "byte = " + bytes);
                        if(bytes != -1) {
                            String msg = new String(buffer, 0, bytes);
                            Log.d("app", msg);
                            MainActivity.this.runOnUiThread(new Runnable() {//쓰레드에서 Toast출력
                                public void run() {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = null;
                                    if(msg.substring(0,4).equals("open")){ //open메시지 받으면 성공 레이아웃으로 설정
                                        layout = inflater.inflate(R.layout.toast_border, (ViewGroup)findViewById(R.id.toast_layout_root));
                                    }
                                    else    //아닐경우 실패 레이아웃으로 설정
                                        layout = inflater.inflate(R.layout.toast_border_fail, (ViewGroup)findViewById(R.id.toast_layout_root));

                                    TextView text = (TextView) layout.findViewById(R.id.toast);
                                    text.setText(msg);  //받은 메시지로 Toast 텍스트 설정

                                    Toast toast = new Toast(context);
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 300);
                                    toast.setView(layout);
                                    toast.show();   //Toast출력
                                }
                            });
                        }
                    } catch (IOException e) {
                        Log.d("app", "err");
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        //바텀 네비게이션 세팅
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
    //소켓통신으로 메시지 전송 메소드
    public void sendMSG(String inputText) throws IOException{
        if(inputText != null) {//메시지가 null이 아닐경우
            byte[] inst = inputText.getBytes();
            output.write(inst); //텍스트 전송
            output.flush();
        }
    }
    //뒤로가기 누를시
    @Override
    public void onBackPressed(){
        try {
            socket.close(); //소켓 닫고
            connect = false;    //연결 끊음 표시
            finish();   //현재 액티비티 종료(이전 실행 activity인 StartActivity로 이동)
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        super.onBackPressed();
    }
}