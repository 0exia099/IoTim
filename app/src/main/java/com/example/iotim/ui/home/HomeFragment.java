package com.example.iotim.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.iotim.MainActivity;
import com.example.iotim.databinding.FragmentHomeBinding;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        EditText e = binding.password;  //화면의 비밀번호 입력 칸 객체

        binding.open.setOnClickListener(new View.OnClickListener() { //open버튼 눌릴시
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() { //새로운 쓰레드로 메시지 전송
                    @Override
                    public void run() {
                        try{
                            //EditText의 값을 MainActivity의 sendMSG메소드를 통해 전송
                            ((MainActivity) MainActivity.context_main).sendMSG(e.getText().toString());
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}