package com.xrest.ridesharing.Response

import com.xrest.ridesharing.model.RegisterModel
import java.util.*

data class DailyEarnings(
        val _id:String?=null,
        val user:RegisterModel,
        val Income:Int?=null,
        val Date:Date?=null
) {
}

data class Rides(
        val _id:String?=null,
        val Rider:RegisterModel?=null,
        val Customer:RegisterModel?=null,
        val From:String?= null,
        val To:String?=null,
        val Fare:Int?=null
)
data class Transaction(
        val _id:String?=null,
        val Sender:RegisterModel?=null,
        val Reciever:RegisterModel?=null,
        val Rides:Rides?=null,
        val Date:Date?=null,
        val Amount:Int?=null,
        val Description:String?=null
)