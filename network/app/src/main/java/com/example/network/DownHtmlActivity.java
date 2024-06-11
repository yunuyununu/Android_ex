package com.example.network;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownHtmlActivity extends AppCompatActivity {

    String html;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView result = (TextView) findViewById(R.id.result);
            result.setText(html);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_html);
        Button btn = findViewById(R.id.down);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        html = downloadHtml(Common.SERVER_URL+"/mobile/main.jsp");
                        handler.sendEmptyMessage(0); //화면변경
                    }
                });
                th.start();
            }
            String downloadHtml(String addr) {
                StringBuffer html = new StringBuffer();
                try {
                    URL url = new URL(addr);
                    HttpURLConnection conn =(HttpURLConnection)url.openConnection(); //서버에 접속 시도
                    if (conn != null) {
                        conn.setConnectTimeout(10000); //접속시도 제한시간
                        conn.setUseCaches(false); //캐시 사용x
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            //  응답코드                200 정상처리
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                            for ( ; ; ) {
                                String line = br.readLine(); //버퍼 한라인 저장
                                if(line == null) break; // 내용 없으면 종료
                                html.append(line + "\n"); //변수 추가
                            }
                            br.close(); //닫기
                        }
                        conn.disconnect(); //접속 종료
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return html.toString(); //리턴
            }
        });
    }
}