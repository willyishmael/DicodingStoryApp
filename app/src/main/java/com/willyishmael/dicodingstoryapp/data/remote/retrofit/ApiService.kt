package com.willyishmael.dicodingstoryapp.data.remote.retrofit

import com.willyishmael.dicodingstoryapp.data.remote.response.GetStoriesResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.LoginResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.Response
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("register")
    fun register(
        @Body requestBody: RequestBody
    ): Call<Response>

    @POST("login")
    fun login(
        @Body requestBody: RequestBody
    ): Call<LoginResponse>

    @POST("stories")
    @Headers("Content-Type: multipart/form-data")
    fun createStories(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Call<Response>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Path("page") page: Int?,
        @Path("size") size: Int?,
        @Path("location") location: Int? = 0
    ): Call<GetStoriesResponse>
}