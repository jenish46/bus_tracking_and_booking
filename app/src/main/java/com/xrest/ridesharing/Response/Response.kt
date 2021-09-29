package com.xrest.ridesharing.Response

class Response {
}
data class TransactionResponse(
val success:Boolean?=null,
        val data:MutableList<Transaction>?=null
)
data class RideResponse(

        val success:Boolean?=null,
        val data:MutableList<Rides>?=null
)