package com.example.myshop

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class JoinActivity : AppCompatActivity() {

    var editId: EditText? =null
    var editPwd: EditText? =null
    var editName: EditText? =null
    var editZipcode: EditText? =null
    var editAddress1: EditText? =null
    var editAddress2: EditText? =null
    var editTel: EditText? =null
    var btnJoin: Button? =null
    var prefs: SharedPreferences? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editId = findViewById(R.id.editId)
        editPwd = findViewById(R.id.editPwd)
        editName = findViewById(R.id.editName)
        editZipcode = findViewById(R.id.editZipcode)
        editAddress1 = findViewById(R.id.editAddress1)
        editAddress2 = findViewById(R.id.editAddress2)
        editTel = findViewById(R.id.editTel)
        btnJoin = findViewById(R.id.btnJoin)
        btnJoin!!.setOnClickListener(View.OnClickListener {
            // 참조변수!! ==> null X
            val id = editId!!.getText().toString()
            val pwd = editPwd!!.getText().toString()
            val name = editName!!.getText().toString()
            val zipcode = editZipcode!!.getText().toString()
            val address1 = editAddress1!!.getText().toString()
            val address2 = editAddress2!!.getText().toString()
            val tel = editTel!!.getText().toString()
            val th = Thread {
                try {
                    val page = "http://192.168.0.26/api/member/join"
                    val url= URL(page)
                    val conn=url.openConnection() as HttpURLConnection
                    val param="userid=$id&passwd=$pwd&name=$name&zipcode=$zipcode&address1=$address1&address2=$address2&tel=$tel"
                    val sb=StringBuilder()
                    if(conn != null) {
                        conn.connectTimeout=3000
                        conn.requestMethod="POST"
                        conn.useCaches=false
                        conn.outputStream.write(param.toByteArray(charset("utf-8")))
                        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                            val br = BufferedReader(InputStreamReader(conn.inputStream,"utf-8"))
                            while(true) {
                                val line=br.readLine()?:break
                                sb.append(line+"\n")
                            }
                            br.close()
                        }
                        conn.disconnect()
                        val jsonObj = JSONObject(sb.toString())
                        val message = jsonObj.getString("message")
                        if(message=="success") {
                            val edit=prefs!!.edit() //환경변수 편집
                            edit.putString("userid",id)
                            edit.putString("name",name)
                            edit.putString("zipcode",zipcode)
                            edit.putString("address1",address1)
                            edit.putString("address2",address2)
                            edit.putString("tel",tel)
                            edit.commit() //xml 업데이트
                            val intent= Intent(this@JoinActivity,MyActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }
            th.start()
        })
    }
}