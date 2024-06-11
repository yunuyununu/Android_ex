package com.example.network;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownImageActivity extends AppCompatActivity {
    ImageView img1; Button btnLoad;
    //String imgUrl = "http://www.google.co.kr/mobile/images/mgc3/homepage1.jpg";
    String imgUrl = Common.SERVER_URL + "/mobile/images/winter.jpg";
    Bitmap bm;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            img1.setImageBitmap(bm);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_image);
        btnLoad = findViewById(R.id.btnLoad);
        img1 = findViewById(R.id.img1);
        btnLoad.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                downImg(imgUrl);
            }
        });
    }
    public void downImg(final String file) { //이미지주소 url
        Thread th = new Thread(new Runnable() {
            public void run() {
                URL url = null;
                try {
                    url = new URL(file); //스트링 ==> url 형식으로
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();  //접속시도
                    InputStream is = conn.getInputStream();
                    //              앱 <== 이미지 리소트
                    bm = BitmapFactory.decodeStream(is);
                    handler.sendEmptyMessage(0);
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
}