package com.xrest.ridesharing.model

import java.io.Serializable

data class RegisterModel(
    val _id : String? = null,
    val FullName: String ? = null,
    val PhoneNumber: String? = null,
    val DateOfBirth: String?=null,
    val Password: String?=null,
    val UserType: String?= null,
    var Latitude: String? = null,
    var Longitude: String? = null,
    var Questions:MutableList<Question>?=null,
    var Rating : MutableList<Rating>? =null,
    var Cash:Int?=null
)
data class Question(
    val question:String?=null,
    val answer:String?=null
): Serializable
data class Rating(
    val user:String?=null,
    val rating: Int? =null
):Serializable