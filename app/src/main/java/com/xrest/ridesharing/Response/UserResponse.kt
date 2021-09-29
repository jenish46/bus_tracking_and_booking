package com.xrest.ridesharing.Response

import com.xrest.ridesharing.model.RegisterModel

data class UserResponse (
    var success:Boolean?=null,
            var data:RegisterModel?=null
    )