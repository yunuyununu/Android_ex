package com.example.myshop

import android.app.AlertDialog
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
import android.widget.Button
import android.widget.EditText
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

class CartActivity : AppCompatActivity() {

    var items:ArrayList<CartDTO>?=null
    var rv:RecyclerView?=null
    var txtSummary: TextView?=null
    var btnClear: Button?=null
    var btnOrder:Button?=null
    var myAdapter: RecyclerView.Adapter<*>?=null
    var prefs: SharedPreferences?=null
    var userid:String?=null
    var money=0
    var delivery=0
    var totalMoney=0
    var count=0
    var handler: Handler = object:Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            var str = ""
            if(count>0) {
                str = String.format("합계금액 : %d원 (배송료 : %d)",money,delivery)
                btnClear!!.visibility = View.VISIBLE
                btnOrder!!.visibility = View.VISIBLE
            } else {
                str="장바구니가 비어있습니다."
                btnClear!!.visibility = View.GONE
                btnOrder!!.visibility = View.GONE
            }
            txtSummary!!.text = str
            myAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_cart)
        prefs= PreferenceManager.getDefaultSharedPreferences(this)
        userid=prefs!!.getString("userid","")
        txtSummary=findViewById(R.id.txtSummary)
        btnClear=findViewById(R.id.btnClear)
        btnOrder=findViewById(R.id.btnOrder)
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
        cartList()
        btnClear!!.setOnClickListener(View.OnClickListener{
            AlertDialog.Builder(this@CartActivity)
                .setMessage("장바구니 비우기")
                .setPositiveButton("Yes"){dialog,which->
                    val th=Thread{
                        try{
                            items=ArrayList()
                            val page = "http://192.168.0.26/api/cart/deleteAll?userid=$userid"
                            val url=URL(page)
                            val conn=url.openConnection() as HttpURLConnection
                            if(conn!=null){
                                conn.connectTimeout=3000
                                conn.useCaches=false
                                if (conn.responseCode == HttpURLConnection.HTTP_OK){
                                }
                                conn.disconnect()
                            }
                            runOnUiThread{ //스레드로 넘겨줌
                                finish()
                                val intent=Intent(this@CartActivity,CartActivity::class.java)
                                startActivity(intent)
                            }
                        }catch(e:Exception){
                            e.printStackTrace()
                        }
                    }
                    th.start()
                }
                .show()
        })
        btnOrder!!.setOnClickListener(View.OnClickListener{
            if(userid==""){
                finish()
                val intent = Intent(this@CartActivity,LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
            val intent = Intent(this@CartActivity,OrderActivity::class.java)
            intent.putExtra("money",money)
            intent.putExtra("delivery",delivery)
            intent.putExtra("totalMoney",totalMoney)
            startActivity(intent)
        })
    }

    fun cartList(){ //장바구니리스트
        val sb=StringBuilder()
        val th=Thread{
            try{
                items=ArrayList()
                val page = "http://192.168.0.26/api/cart/list?userid=$userid"
                val url=URL(page)
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
                //스트링=>json 변환
                money=jsonObj.getInt("money") //key값
                delivery=jsonObj.getInt("delivery")
                totalMoney=jsonObj.getInt("totalMoney")
                count=jsonObj.getInt("count")
                val arr=jsonObj["list"] as JSONArray //json배열
                for(i in 0 until arr.length()) {
                    val row = arr.getJSONObject(i)
                    val dto=CartDTO()
                    dto.cartId=row.getInt("cartId")
                    dto.productCode=row.getInt("productCode")
                    dto.productName=row.getString("productName")
                    dto.amount=row.getInt("amount")
                    dto.price=row.getInt("price")
                    items!!.add(dto)
                }
                // 백그라운드에서 화면 변경 ==> handler , runOnUiThread
                handler.sendEmptyMessage(0)
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
        th.start()
    }

    fun onClick(v:View){
        finish()
        var intent:Intent?=null
        if(v.id==R.id.btnMy){
            intent=Intent(this,MyActivity::class.java)
        }else if(v.id==R.id.btnProduct){
            intent=Intent(this,ProductActivity::class.java)
        }else if(v.id==R.id.btnOrderList){
            intent=Intent(this,OrderListActivity::class.java)
        }
        startActivity(intent)
    }

    internal inner class MyAdapter:RecyclerView.Adapter<MyAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder{
            val rowItem = LayoutInflater.from(parent.context).inflate(R.layout.cart_row,parent,false)
            return ViewHolder(rowItem)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            val str=items!![position].productName+""+items!![position].price+"원"
            holder.txtProduct.text=str
            holder.editAmount.setText(items!![position].amount.toString() +"")
        }
        override fun getItemCount():Int{
            return items!!.size
        }
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val txtProduct:TextView
            val editAmount: EditText
            private val btnPlus:Button
            private val btnMinus:Button
            private val btnUpdate:Button
            private val btnDelete:Button
            init{
                txtProduct=view.findViewById(R.id.txtProduct)
                editAmount=view.findViewById(R.id.editAmount)
                btnPlus=view.findViewById(R.id.btnPlus)
                btnMinus=view.findViewById(R.id.btnMinus)
                btnUpdate=view.findViewById(R.id.btnUpdate)
                btnDelete=view.findViewById(R.id.btnDelete)
                btnPlus.setOnClickListener{
                    var num=editAmount.text.toString().toInt()
                    num++
                    editAmount.setText(num.toString()+"")
                }
                btnMinus.setOnClickListener{
                    var num=editAmount.text.toString().toInt()
                    if(num>1){
                        num--
                        editAmount.setText(num.toString()+"")
                    }
                }
                btnUpdate.setOnClickListener{
                    if(userid==""){
                        finish()
                        val intent=Intent(this@CartActivity,LoginActivity::class.java)
                        startActivity(intent)
                    }
                    val pos=layoutPosition
                    val cartId=items!![pos].cartId
                    val productCode=items!![pos].productCode
                    val amount=editAmount.text.toString().toInt()
                    val th=Thread{
                        try{
                            val page = "http://192.168.0.26/api/cart/update"
                            val url = URL(page)
                            val conn = url.openConnection() as HttpURLConnection
                            val param = "userid=$userid&cartId=$cartId&productCode=$productCode&amount=$amount"
                            if(conn != null){
                                conn.connectTimeout=3000
                                conn.requestMethod="POST"
                                conn.useCaches=false
                                conn.outputStream.write(param.toByteArray(charset("utf-8")))
                                if(conn.responseCode == HttpURLConnection.HTTP_OK){
                                }
                                conn.disconnect()
                            }
                            runOnUiThread{
                                finish()
                                val intent=Intent(this@CartActivity,CartActivity::class.java)
                                startActivity(intent)
                            }
                        }catch(e:Exception){
                            e.printStackTrace()
                        }
                    }
                    th.start()
                }
                btnDelete.setOnClickListener{
                    AlertDialog.Builder(this@CartActivity)
                        .setMessage("선택하신 상품을 장바구니에서 삭제할까요?")
                        .setPositiveButton("Yes"){dialog,which->
                            if(userid==""){
                                finish()
                                val intent=Intent(this@CartActivity,LoginActivity::class.java)
                                startActivity(intent)
                            }
                            val pos=layoutPosition
                            val cartId=items!![pos].cartId
                            val th=Thread{
                                try{
                                    val page = "http://192.168.0.26/api/cart/delete/$cartId"
                                    val url = URL(page)
                                    val conn = url.openConnection() as HttpURLConnection
                                    if(conn != null){
                                        conn.connectTimeout = 3000
                                        conn.requestMethod = "GET"
                                        conn.useCaches = false
                                        if (conn.responseCode == HttpURLConnection.HTTP_OK){
                                        }
                                        conn.disconnect()
                                    }
                                    runOnUiThread {
                                        finish()
                                        val intent = Intent(this@CartActivity, CartActivity::class.java)
                                        startActivity(intent)
                                    }
                                } catch (e:Exception){
                                    e.printStackTrace()
                                }
                            }
                            th.start()
                        }
                        .show()
                }
            }
        }
    }
}