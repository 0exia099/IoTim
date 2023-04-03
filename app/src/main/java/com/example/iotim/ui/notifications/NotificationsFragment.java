package com.example.iotim.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.iotim.MainActivity;
import com.example.iotim.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        WebView webView = binding.webView; //화면의 webView객체

        webView.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
        webView.getSettings().setLoadWithOverviewMode(true);//스크린 크기에 맞추기
        webView.getSettings().setUseWideViewPort(true);//wide viewport설정
        webView.setWebViewClient(new WebViewClient());//웹뷰 요청 조작

        //영상 url
        String url ="http://" + ((MainActivity) MainActivity.context_main).ip + ":8080/stream/video.mjpeg";
        webView.loadUrl(url);//WebView에 해당 url출력

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}