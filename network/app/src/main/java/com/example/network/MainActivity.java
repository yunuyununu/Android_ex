package com.example.network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onclick(View v) {
        Intent intent = null;
        if(v.getId() == R.id.button1) {
            intent = new Intent(this, NetworkStatusActivity.class);
        } else if (v.getId() == R.id.button2) {
            intent = new Intent(this, DownHtmlActivity.class);
        } else if (v.getId() == R.id.button3) {
            intent = new Intent(this, DownImageActivity.class);
        } else if (v.getId() == R.id.button4) {
            intent = new Intent(this, UploadActivity.class);
        } else if (v.getId() == R.id.button5) {
            intent = new Intent(this, BookListActivity.class);
        } else if (v.getId() == R.id.button6) {
            intent = new Intent(this, BookListJsonActivity.class);
        } else if (v.getId() == R.id.button7) {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
    }
}