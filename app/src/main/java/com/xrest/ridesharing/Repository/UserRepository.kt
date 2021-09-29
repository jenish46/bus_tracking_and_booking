package com.xrest.ridesharing.Repository

import com.xrest.ridesharing.API.RetrofitService
import com.xrest.ridesharing.API.HandelApiRequest
import com.xrest.ridesharing.API.UserRoutes
import com.xrest.ridesharing.CommonResponse
import com.xrest.ridesharing.Response.LoginResponse
import com.xrest.ridesharing.Response.UserResponse
import com.xrest.ridesharing.model.RegisterModel

class UserRepository: HandelApiRequest() {
    val myApi = RetrofitService.buildService(UserRoutes::class.java)

    suspend fun loginUser(PhoneNumber: String, Password: String):LoginResponse{
        return apiRequest {
            myApi.LoginUser(PhoneNumber, Password)
        }
    }

    suspend fun registerUser(userDetail: RegisterModel):CommonResponse{
        return  apiRequest {
            myApi.RegisterUser(userDetail)
        }
    }

    suspend fun getUser(id:String):UserResponse{
        return apiRequest {
            myApi.getUser(id)
        }
    }

}