package com.example.myshop

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    var txtResult: TextView? = null
    var editId: EditText? = null
    var editPwd: EditText? = null
    var result=""
    var prefs: SharedPreferences? = null
    var name:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editId = findViewById(R.id.editId)
        editPwd = findViewById(R.id.editPwd)
        val intent = intent
        val option = intent.getStringExtra("option")
        if(option != null && option.equals("logout")){
            val edit = prefs!!.edit() //편집모드로 바꾸고
            edit.putString("userid","")
            edit.putString("name","")
            edit.commit() //xml 변경
            finish() //현재화면종료
            startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
        }
    }

    fun onClick(v: View) {
        if(v.id==R.id.btnLogin) {
            val th = Thread {
                try {
                    val page = "http://192.168.0.26/api/member/login"
                    val url = URL(page)
                    val conn = url.openConnection() as HttpURLConnection
                    val param = "userid="+editId!!.text.toString()+"&passwd="+editPwd!!.text.toString()
                    val sb = StringBuilder()
                    if(conn != null) {
                        conn.connectTimeout = 3000
                        conn.requestMethod = "POST"
                        conn.useCaches = false
                        conn.outputStream.write(param.toByteArray(charset("utf-8")))
                        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                            val br = BufferedReader(InputStreamReader(conn.inputStream,"utf-8"))
                            while(true){
                                val line=br.readLine()?:break
                                sb.append(line+"\n")
                            }
                            br.close()
                        }
                        conn.disconnect()
                    }
                    val jsonObj = JSONObject(sb.toString())
                    val message = jsonObj.getString("message")
                    if (message.equals("success")) { //.equals 사용
                        name = jsonObj.getString("name")
                        var msg=""
                        result = if (name != null && name != "null") {
                            name + "님 환영합니다."
                        } else {
                            "아이디 또는 비밀번호가 일치하지 않습니다."
                        }
                        val edit = prefs!!.edit()
                        edit.putString("userid", editId!!.text.toString())
                        edit.putString("name", name)
                        edit.commit()
                        val intent = Intent(this@LoginActivity, ProductActivity::class.java)
                        startActivity(intent)
                    } else if (message.equals("error")) {
                        Log.i("test","error")
                        runOnUiThread{ Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show() }
                    }

                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }
            th.start()
        } else if (v.id == R.id.btnJoin) {
            finish()
            val intent = Intent(this@LoginActivity, JoinActivity::class.java)
            startActivity(intent)
        }
    }
}