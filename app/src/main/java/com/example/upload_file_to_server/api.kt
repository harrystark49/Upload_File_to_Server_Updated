package com.example.upload_file_to_server

import okhttp3.MultipartBody
import okhttp3.Response
import retrofit2.http.POST
import retrofit2.http.Part

interface api {
    @POST("photos/")
    fun postData(
        @Part file:MultipartBody.Part
    ):Response
}