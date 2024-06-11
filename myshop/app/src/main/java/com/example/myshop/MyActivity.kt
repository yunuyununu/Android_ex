package com.example.myshop

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.TextView

class MyActivity : AppCompatActivity() {

    var prefs: SharedPreferences?=null
    var txtMessage: TextView?=null
    var btnLogin: Button?=null
    var btnLogout: Button?=null
    var userid:String?=null
    var name:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        userid = prefs!!.getString("userid","")
        name = prefs!!.getString("name","")
        txtMessage = findViewById(R.id.txtMessage)
        if(name!=""){
            txtMessage!!.setText(name+"님 환영합니다.")
        }
        btnLogin = findViewById(R.id.btnLogin)
        btnLogout = findViewById(R.id.btnLogout)
        if(userid ==""){
            btnLogin!!.setVisibility(View.VISIBLE) //표시
            btnLogout!!.setVisibility(View.GONE) //숨김(=INVISIBLE 자리는 차지함/공백으로)
        } else{
            btnLogin!!.setVisibility(View.GONE)
            btnLogout!!.setVisibility(View.VISIBLE)
        }
    }

    fun onClick(v:View){
        var intent: Intent?=null
        if(v.id==R.id.btnLogin){
            intent = Intent(this,LoginActivity::class.java)
            intent.putExtra("option","login")
        }else if (v.id==R.id.btnLogout){
            intent=Intent(this,LoginActivity::class.java)
            intent.putExtra("option","logout")
        }else if(v.id==R.id.btnProduct){
            intent=Intent(this,ProductActivity::class.java)
        }else if(v.id==R.id.btnCart){
            intent=Intent(this,CartActivity::class.java)
        }
        else if(v.id==R.id.btnOrderList){
            intent=Intent(this,OrderListActivity::class.java)
        }
        startActivity(intent)
    }
}