package com.xrest.ridesharing.Response

import com.xrest.ridesharing.model.RegisterModel

data class LoginResponse(
    val success: Boolean? = null,
    val token: String?=null,
    val data: RegisterModel?= null

)