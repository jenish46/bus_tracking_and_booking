package com.xrest.ridesharing.API

import com.xrest.ridesharing.CommonResponse
import com.xrest.ridesharing.Response.LoginResponse
import com.xrest.ridesharing.Response.UserResponse
import com.xrest.ridesharing.model.RegisterModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserRoutes {

    @FormUrlEncoded
    @POST("/userLogin")
    suspend fun LoginUser(
        @Field("PhoneNumber") PhoneNumber:String,
        @Field("Password") Password:String,

    ): Response<LoginResponse>

    @POST("/userRegister")
    suspend fun RegisterUser(
            @Body userDetail: RegisterModel
    ): Response<CommonResponse>

    @GET("/get/{id}")
    suspend fun getUser(@Path("id")id:String):Response<UserResponse>

    @PUT("/profile/{id}")
    suspend fun profile(@Path("id")id:String, @Part body:MultipartBody.Part):Response<CommonResponse>

    @FormUrlEncoded
    @PUT("/rate/{id}")
    suspend fun rate(@Path("id")id:String,@Field("Rating")rate:Double, @Header("Authorization")token:String):Response<CommonResponse>
}