package com.example.myshop

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class OrderListActivity : AppCompatActivity() {

    var items:ArrayList<OrderDTO>?=null
    var rv: RecyclerView?=null
    var txtSummary: TextView?=null
    var myAdapter:RecyclerView.Adapter<*>?=null
    var prefs: SharedPreferences?=null
    var userid:String?=null
    var sumMoney=0
    var fee=0
    var sum=0
    var count=0
    var message:String?=null
    var handler: Handler =object:Handler(){
        override fun handleMessage(msg: Message){
            super.handleMessage(msg)
            myAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_order_list)
        prefs= PreferenceManager.getDefaultSharedPreferences(this)
        userid=prefs!!.getString("userid","")
        txtSummary=findViewById(R.id.txtSummary)
        rv=findViewById(R.id.rv)
        rv!!.setLayoutManager(
            LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        )
        rv!!.addItemDecoration(
            DividerItemDecoration(
                rv!!.getContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        myAdapter=MyAdapter()
        rv!!.setAdapter(myAdapter)
        val sb=StringBuilder()
        val th=Thread{
            try{
                items=ArrayList()
                val page = "http://192.168.0.26/api/order/list?userid=$userid"
                val url= URL(page)
                val conn=url.openConnection() as HttpURLConnection
                        if(conn!=null){
                            conn.connectTimeout=3000
                            conn.useCaches=false
                            if(conn.responseCode==HttpURLConnection.HTTP_OK){
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
                val jsonObj= JSONObject(sb.toString())
                val arr=jsonObj["list"] as JSONArray
                        for(i in 0 until arr.length()){
                    val row=arr.getJSONObject(i)
                    val dto=OrderDTO()
                    dto.orderIdx=row.getInt("orderIdx")
                    dto.orderDate=row.getString("orderDate")
                    dto.totalMoney=row.getInt("totalMoney")
                    dto.status=row.getString("status")
                    items!!.add(dto)
                }
                handler.sendEmptyMessage(0)
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
        th.start()
    }

    fun onClick(v: View){
        finish()
        var intent: Intent?=null
        if(v.id==R.id.btnMy){
            intent=Intent(this,MyActivity::class.java)
        }else if(v.id==R.id.btnProduct){
            intent=Intent(this,ProductActivity::class.java)
        }else if(v.id==R.id.btnCart){
            intent=Intent(this,CartActivity::class.java)
        }
        startActivity(intent)
    }

    internal inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder{
            val rowItem=
                LayoutInflater.from(parent.context).inflate(R.layout.order_row,parent,false)
            return ViewHolder(rowItem)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            val str="""
                    주문일자:${items!![position].orderDate}
                    금액:${items!![position].totalMoney}
                    상태:${items!![position].status}
                    """.trimIndent()
            holder.txtDetail.text=str
        }
        override fun getItemCount():Int{
            return items!!.size
        }
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val txtDetail:TextView
            init{
                txtDetail=view.findViewById(R.id.txtDetail)
                view.setOnClickListener{
                    finish()
                    val intent=Intent(this@OrderListActivity,OrderDetailActivity::class.java)
                    intent.putExtra("orderIdx", items!![layoutPosition].orderIdx)
                    startActivity(intent)
                }
            }
        }
    }
}