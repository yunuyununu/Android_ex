package com.example.myshop

class OrderDTO {
    @JvmField
    var orderIdx=0
    var userid:String?=null
    var cancelReason:String?=null
    var money=0
    var delivery=0

    @JvmField
    var totalMoney=0

    @JvmField
    var orderDate:String?=null

    @JvmField
    var status:String?=null
    override fun toString():String{
        return "OrderDTO{"+
                "orderIdx="+orderIdx+
                ",userid='"+userid+'\''+
                ",cancelReason='"+cancelReason+'\''+
                ",money="+money+
                ",delivery="+delivery+
                ",totalMoney="+totalMoney+
                ",orderDate='"+orderDate+'\''+
                ",status='"+status+'\''+
                '}'
    }
}