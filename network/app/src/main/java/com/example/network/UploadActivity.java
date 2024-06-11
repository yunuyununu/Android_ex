package com.example.network;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadActivity extends AppCompatActivity {
    Button btn;
    EditText edit_entry;
    FileInputStream fis;
    URL url;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String result;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            edit_entry.setText(result);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        edit_entry = findViewById(R.id.edit_entry);
        makeFile();
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String file = "/data/data/" + getPackageName() + "/files/test.txt"; //내장메모리
                    fileUpload(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void makeFile() {
        String path = "/data/data/" + getPackageName() + "/files";
        File dir = new File(path);
        if (!dir.exists()) { //디렉토리 있으면 True 없으면 false
            dir.mkdir();
        }
        File file = new File("/data/data/" + getPackageName() + "/files/test.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file); //파일생성
            String str = "hello android 안녕 안드로이드 하이하이";
            fos.write(str.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void fileUpload(final String file) { //앱 => 서버
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                httpFileUpload(Common.SERVER_URL + "/mobile/upload.do", file);
            }
        });
        th.start();
    }

    void httpFileUpload(String urlString, String file) {
        try {
            fis = new FileInputStream(file);
            //저장된 파일 읽기
            url = new URL(urlString);
            //스트링 => url형식
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //접속객체
            conn.setDoInput(true); //서버 입력가능
            conn.setDoOutput(true); //서버 출력가능
            conn.setUseCaches(false); //캐시 사용x
            conn.setRequestMethod("POST"); //post방식
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //서버에 출력
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            // form-data 앞에 공백이 있어야 합니다.
            String[] arr = file.split("/"); //쪼개기(구분자)
            String file_name = arr[arr.length - 1];
            dos.writeBytes("Content-Disposition: form-data;name=\"file\";filename=\"" + file_name + "\"" + lineEnd);
            //따옴표 속 따옴표 = \"
            dos.writeBytes(lineEnd);
            int bytesAvailable = fis.available(); //스트림 최대 바이트수
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize]; //바이트배열
            int bytesRead = fis.read(buffer, 0, bufferSize);
            //                      버퍼      시작      사이즈
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            fis.close(); //스트림 닫기
            dos.flush(); //버퍼 비우기

            //--------여기까지 업로드 작업 (서버처리 -> 완료 -> 메세지전달)
            int ch;
            InputStream is = conn.getInputStream(); //서버의 메시지
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) { //바이트 읽어서 내용없으면 -1
                b.append((char) ch);
            }
            result = b.toString().trim(); //공백제거
            dos.close(); //스트림 닫기
            handler.sendEmptyMessage(0); //핸들러요청
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}