package com.example.myshop

class CartDTO {
    var cartId = 0
    var userid: String? = null
    var productCode = 0
    var productName: String? = null
    var amount = 0
    var price = 0
    override fun toString(): String {
        //      함수이름 : 리턴타입
        return "CartDTO{" +
                "cartId=" + cartId +
                ", userid='" + userid + '\'' +
                ", productCode=" + productCode +
                ", productName=" + productName +
                ", amount=" + amount +
                ", price=" + price +
                '}'
    }
    //alt + insert
}