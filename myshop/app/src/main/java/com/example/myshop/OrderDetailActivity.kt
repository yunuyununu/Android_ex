package com.example.myshop

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class OrderDetailActivity : AppCompatActivity() {

    var txtSummary: TextView?=null
    var prefs: SharedPreferences?=null
    var userid:String?=null
    var zipcode:String?=null
    var address1:String?=null
    var address2:String?=null
    var tel:String?=null
    var cancelReason:String?=null
    var status:String?=null
    var orderIdx=0
    var items:MutableList<OrderDetailDTO> = ArrayList()
    var btnCancel: Button?=null
    var editCancelReason: EditText?=null
    var rv: RecyclerView?=null
    var myAdapter:RecyclerView.Adapter<*>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        prefs= PreferenceManager.getDefaultSharedPreferences(this)
        userid=prefs!!.getString("userid","")
        zipcode=prefs!!.getString("zipcode","")
        address1=prefs!!.getString("address1","")
        address2=prefs!!.getString("address2","")
        tel=prefs!!.getString("tel","")
        txtSummary=findViewById(R.id.txtSummary)
        btnCancel=findViewById(R.id.btnCancel)
        editCancelReason=findViewById(R.id.editCancelReason)
        rv=findViewById(R.id.rv)
        rv!!.setLayoutManager(LinearLayoutManager(this, RecyclerView.VERTICAL,false))
        rv!!.addItemDecoration(
            DividerItemDecoration(
                rv!!.getContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        myAdapter=MyAdapter()
        rv!!.setAdapter(myAdapter)
        val intent=intent
        orderIdx=intent.getIntExtra("orderIdx",0)
        val th=Thread{
            try{
                val page = "http://192.168.0.26/api/order/detail/$orderIdx"
                val url= URL(page)
                val conn=url.openConnection() as HttpURLConnection
                val sb=StringBuilder()
                if(conn!=null){
                    conn.connectTimeout=3000
                    conn.requestMethod="POST"
                    conn.useCaches=false
                    //conn.getOutputStream().write(param.getBytes("utf-8 "));
                    if(conn.responseCode== HttpURLConnection.HTTP_OK){
                        val br= BufferedReader(
                            InputStreamReader(
                                conn.inputStream,"utf-8"
                            )
                        )
                        while(true){
                            val line=br.readLine()?:break
                            sb.append(line+"\n")
                        }
                        br.close()
                    }
                    conn.disconnect()
                }
                Log.i("test","json:$sb")
                val jsonObj= JSONObject(sb.toString())
                val dto=jsonObj.getJSONObject("dto")
                val orderDate=dto.getString("orderDate")
                val method=dto.getString("method")
                status=dto.getString("status")
                val cardNumber=dto.getString("cardNumber")
                val zipcode=dto.getString("zipcode")
                val address1=dto.getString("address1")
                val address2=dto.getString("address2")
                val tel=dto.getString("tel")
                val cancelReason=dto.getString("cancelReason")
                val money=dto.getInt("money")
                val delivery=dto.getInt("delivery")
                val totalMoney=dto.getInt("totalMoney")
                val arr=jsonObj.getJSONArray("detailList")
                for(i in 0 until arr.length()){
                    val obj=arr.getJSONObject(i)
                    val row=OrderDetailDTO()
                    row.productCode=obj.getInt("productCode")
                    row.productName=obj.getString("productName")
                    row.price=obj.getInt("price")
                    row.amount=obj.getInt("amount")
                    row.money=obj.getInt("money")
                    items.add(row)
                }
                runOnUiThread{
                    var str="""
                        주문일자:$orderDate
                        결제방법:$method
                        카드번호:$cardNumber
                        배송주소:$address1$address2($zipcode)
                        전화번호:$tel
                        주문금액:$money
                        배송료:$delivery
                        총금액:$totalMoney
                        처리상태:$status"""
                    if(status=="주문취소요청"||status=="주문취소완료"){
                        str+="\n취소사유:$cancelReason"
                    }
                    txtSummary!!.setText(str)
                    myAdapter!!.notifyDataSetChanged()
                    if(status=="주문접수"){
                        btnCancel!!.setVisibility(View.VISIBLE)
                    } else {
                        btnCancel!!.setVisibility(View.GONE)
                    }
                }
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
        th.start()
        btnCancel!!.setOnClickListener(View.OnClickListener{
            editCancelReason!!.setVisibility(View.VISIBLE)
            if (editCancelReason!!.getText().toString().toByteArray().size==0){
                Toast.makeText(this@OrderDetailActivity, "취소 사유를 입력하세요.",Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            AlertDialog.Builder(this@OrderDetailActivity)
            .setMessage("취소요청하시겠습니까?")
            .setPositiveButton("Yes"){dialog,which->
                val th=Thread{
                    try{
                        val page = "http://192.168.0.26/api/order/cancel"
                        val url=URL(page)
                        val conn=url.openConnection() as HttpURLConnection
                        cancelReason = editCancelReason!!.getText().toString()
                        val param= "userid=$userid&orderIdx=$orderIdx&cancelReason=$cancelReason"
                        if(conn!=null){
                            conn.connectTimeout=3000
                            conn.requestMethod="POST"
                            conn.useCaches=false
                            conn.outputStream.write(param.toByteArray
                            (charset("utf-8")))
                            if (conn.responseCode == HttpURLConnection.HTTP_OK){
                            }
                            conn.disconnect()
                        }
                        runOnUiThread{
                            finish()
                            val intent= Intent(
                                this@OrderDetailActivity,
                                OrderDetailActivity::class.java
                            )
                            intent.putExtra("orderIdx",orderIdx)
                            startActivity(intent)
                        }
                    } catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                th.start()
            }
            .show()
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
    internal inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder{
            val rowItem= LayoutInflater.from(parent.context).inflate(R.layout.order_row,parent,false)
            return ViewHolder(rowItem)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            val dto=items[position]
            val str=String.format(
                "품명:%s,단가:%d,수량:%d,금액:%d",
                dto.productName,
                dto.price,
                dto.amount,
                dto.money
            )
            holder.txtDetail.text=str
        }
        override fun getItemCount():Int{
            return items.size
        }
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val txtDetail:TextView
            init{
                txtDetail=view.findViewById(R.id.txtDetail)
            }
        }
    }
}