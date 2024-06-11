package com.example.myshop

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class ProductActivity : AppCompatActivity() {
    var items: ArrayList<ProductDTO>? = null
    var rv: RecyclerView? = null
    var myAdapter: RecyclerView.Adapter<*>? = null
    var prefs: SharedPreferences? = null
    var userid: String? = null
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            myAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        userid = prefs!!.getString("userid", "")
        rv = findViewById(R.id.rv)
        rv!!.setLayoutManager(LinearLayoutManager(this, RecyclerView.VERTICAL, false))
        rv!!.addItemDecoration(
            DividerItemDecoration(
                rv!!.getContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        myAdapter = MyAdapter()
        rv!!.adapter = myAdapter
        val sb = StringBuilder()
        val th = Thread {
            try {
                items = ArrayList()
                val page = "http://192.168.0.26/api/product/list"
                Log.i("test",page)
                val url = URL(page)
                val conn = url.openConnection() as HttpURLConnection
                if (conn != null) {
                    conn.connectTimeout = 3000
                    conn.useCaches = false
                    if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                        val br = BufferedReader(InputStreamReader(conn.inputStream, "utf-8"))
                        while (true) {
                            val line = br.readLine() ?: break
                            sb.append(line + "\n")
                        }
                        br.close()
                    }
                    conn.disconnect()
                }
                Log.i("test", "data:$sb")
                val arr = JSONArray(sb.toString())
                for (i in 0 until arr.length()) {
                    val row = arr.getJSONObject(i)
                    val dto = ProductDTO()
                    dto.productName = row.getString("productName")
                    dto.productCode = row.getInt("productCode")
                    dto.price = row.getInt("price")
                    dto.fileName = row.getString("fileName")
                    items!!.add(dto)
                }
                handler.sendEmptyMessage(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        th.start()
    }


    fun onClick(v: View) {
        finish()
        var intent: Intent? = null
        if (v.id == R.id.btnMy) {
            intent = Intent(this, MyActivity::class.java)
        } else if (v.id == R.id.btnCart) {
            intent = Intent(this, CartActivity::class.java)
        }
        else if (v.id == R.id.btnOrderList) {
            intent = Intent(this, OrderListActivity::class.java)
        }
        startActivity(intent)
    }

    fun setImage(holder: MyAdapter.ViewHolder, pos: Int) {
        val th = Thread {
            var url: URL? = null
            try {
                url = URL("http://192.168.0.26/images/" + items!![pos].fileName)
                val conn = url.openConnection() as HttpURLConnection
                conn.connect()
                val `is` = conn.inputStream
                val bm = BitmapFactory.decodeStream(`is`)
                runOnUiThread { holder.imgProduct.setImageBitmap(bm) }
                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        th.start()
    }


    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val rowItem =
                LayoutInflater.from(parent.context).inflate(R.layout.product_row, parent, false)
            return ViewHolder(rowItem)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val str = items!![position].productName + "(" + items!![position].price + ")"
            holder.txtProduct.text = str
            setImage(holder, position)
        }

        override fun getItemCount(): Int {
            return items!!.size
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgProduct: ImageView
            val txtProduct: TextView
            private val editAmount: EditText
            private val btnPlus: Button
            private val btnMinus: Button
            private val btnCart: Button
            private val btnOrder: Button

            init {
                imgProduct = view.findViewById(R.id.imgProduct)
                txtProduct = view.findViewById(R.id.txtProduct)
                btnPlus = view.findViewById(R.id.btnPlus)
                btnMinus = view.findViewById(R.id.btnMinus)
                btnCart = view.findViewById(R.id.btnCart)
                btnOrder = view.findViewById(R.id.btnOrder)
                editAmount = view.findViewById(R.id.editAmount)
                btnPlus.setOnClickListener {
                    var num = editAmount.text.toString().toInt()
                    num++
                    editAmount.setText(num.toString() + "")
                }
                btnMinus.setOnClickListener {
                    var num = editAmount.text.toString().toInt()
                    if (num > 1) {
                        num--
                        editAmount.setText(num.toString() + "")
                    }
                }
                btnCart.setOnClickListener {
                    if (userid == "") {
                        finish()
                        val intent = Intent(this@ProductActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    val pos = layoutPosition
                    val productCode = items!![pos].productCode
                    val amount = editAmount.text.toString().toInt()
                    val th = Thread {
                        try {
                            val page = "http://192.168.0.26/api/cart/insert"
                            val url = URL(page)
                            val conn = url.openConnection() as HttpURLConnection

                            val param = "userid=$userid&productCode=$productCode&amount=$amount"
                            if (conn != null) {
                                conn.connectTimeout = 3000
                                conn.requestMethod = "POST"
                                conn.useCaches = false
                                conn.outputStream.write(param.toByteArray(charset("utf-8")))
                                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                                }
                                conn.disconnect()
                                runOnUiThread {
                                    finish()
                                    val intent =
                                        Intent(this@ProductActivity, CartActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    th.start()
                }
                btnOrder.setOnClickListener {
                    if (userid == "") {
                        finish()
                        val intent = Intent(this@ProductActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    val pos = layoutPosition
                    val productCode = items!![pos].productCode
                    val price = items!![pos].price
                    val amount = editAmount.text.toString().toInt()
                    val money = price * amount
                    val delivery = if (money >= 30000) 0 else 2500
                    val totalMoney = money + delivery
                    finish()
                    val intent = Intent(this@ProductActivity, OrderActivity::class.java)
                    intent.putExtra("productCode", productCode)
                    intent.putExtra("amount", amount)
                    intent.putExtra("money", money)
                    intent.putExtra("delivery", delivery)
                    intent.putExtra("totalMoney", totalMoney)
                    startActivity(intent)
                }
            }
        }
    }
}