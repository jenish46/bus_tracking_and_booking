package com.xrest.ridesharing.API

import com.xrest.ridesharing.model.RegisterModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitService {



     val BASE_URL = "http://192.168.0.108:2021"
    var user: RegisterModel? = null

    var token: String? = null

    private val okHttp = OkHttpClient.Builder()

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).client(
            okHttp.build()

        )

    private val retrofit = retrofitBuilder.build()

    //Generic class
    fun <T> buildService(ServiceType: Class<T>): T {
        return retrofit.create(ServiceType)
    }
}