package com.example.myshop

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import java.net.HttpURLConnection
import java.net.URL

class OrderActivity : AppCompatActivity() {

    var txtProduct: TextView?=null
    var editCardNumber: EditText?=null
    var editZipcode:EditText?=null
    var editAddress1:EditText?=null
    var editAddress2:EditText?=null
    var editTel:EditText?=null
    var rdoCard: RadioButton?=null
    var rdoAccount:RadioButton?=null
    var rdoMethod: RadioGroup?=null
    var btnOrder: Button?=null
    var prefs: SharedPreferences?=null
    var userid:String?=null
    var method:String?=null
    var zipcode:String?=null
    var address1:String?=null
    var address2:String?=null
    var tel:String?=null
    var cardNumber:String?=null
    var productCode=0
    var amount=0
    var money=0
    var delivery=0
    var totalMoney=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        prefs= PreferenceManager.getDefaultSharedPreferences(this)
        userid=prefs!!.getString("userid","")
        zipcode=prefs!!.getString("zipcode","")
        address1=prefs!!.getString("address1","")
        address2=prefs!!.getString("address2","")
        tel=prefs!!.getString("tel","")
        txtProduct=findViewById(R.id.txtProduct)
        editCardNumber=findViewById(R.id.editCardNumber)
        editZipcode=findViewById(R.id.editZipcode)
        editAddress1=findViewById(R.id.editAddress1)
        editAddress2=findViewById(R.id.editAddress2)
        editTel=findViewById(R.id.editTel)
        rdoCard=findViewById(R.id.rdoCard)
        rdoAccount=findViewById(R.id.rdoAccount)
        rdoMethod=findViewById(R.id.rdoMethod)
        btnOrder=findViewById(R.id.btnOrder)
        val intent=intent
        productCode=intent.getIntExtra("productCode",0)
        amount=intent.getIntExtra("amount",0)
        money=intent.getIntExtra("money",0)
        delivery=intent.getIntExtra("delivery",0)
        totalMoney=intent.getIntExtra("totalMoney",0)
        val str=String.format("합계금액:%d(배송료:%d)",totalMoney,delivery)
        txtProduct!!.setText(str)
        editZipcode!!.setText(zipcode)
        editAddress1!!.setText(address1)
        editAddress2!!.setText(address2)
        editTel!!.setText(tel)
        rdoMethod!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            if(rdoCard!!.isChecked()){
                editCardNumber!!.setVisibility(View.VISIBLE)
                method="card"
            }else if(rdoAccount!!.isChecked()){
                editCardNumber!!.setVisibility(View.GONE)
                method="account"
            }
        })
        btnOrder!!.setOnClickListener(View.OnClickListener{
            if(userid==""){
                finish()
                val intent= Intent(this@OrderActivity,LoginActivity::class.java)
                startActivity(intent)
            }
            val th=Thread{
                try{
                    val page="http://192.168.0.26/api/order/insert"
                    val url= URL(page)
                    val conn=url.openConnection() as HttpURLConnection
                    cardNumber=editCardNumber!!.getText().toString()
                    zipcode=editZipcode!!.getText().toString()
                    address1=editAddress1!!.getText().toString()
                    address2=editAddress2!!.getText().toString()
                    tel=editTel!!.getText().toString()
                    method=if(rdoCard!!.isChecked())"card" else "account"
                    val param=("userid="+userid+"&money="+money
                            + "&delivery=" + delivery + "&totalMoney=" +
                            totalMoney
                            +"&productCode="+productCode
                            +"&amount="+amount+"&method="+method+
                            "&cardNumber="
                            +cardNumber+"&zipcode="+zipcode+"&address1="+address1
                            +"&address2="+address2+"&tel="+tel)
                    if(conn!=null){
                        conn.connectTimeout=3000
                        conn.requestMethod="POST"
                        conn.useCaches=false
                        conn.outputStream.write(param.toByteArray(charset
                            ("utf-8")))
                        if (conn.responseCode == HttpURLConnection.HTTP_OK){
                        }
                        conn.disconnect()
                    }
                    runOnUiThread{
                        finish()
                        val intent=Intent(this@OrderActivity,OrderListActivity::class.java)
                        startActivity(intent)
                    }
                }catch(e:Exception){
                    e.printStackTrace()
                }
            }
            th.start()
        })
    }

    fun onClick(v:View){
        finish()
        var intent:Intent?=null
        if(v.id==R.id.btnMy){
            intent=Intent(this,MyActivity::class.java)
        }else if(v.id==R.id.btnProduct){
            intent=Intent(this,ProductActivity::class.java)
        }else if(v.id==R.id.btnCart){
            intent=Intent(this,CartActivity::class.java)
        }else if(v.id==R.id.btnOrderList){
            intent=Intent(this,OrderListActivity::class.java)
        }
        startActivity(intent)
    }
}