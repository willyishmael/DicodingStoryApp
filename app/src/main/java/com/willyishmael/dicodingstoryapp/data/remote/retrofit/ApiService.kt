package com.willyishmael.dicodingstoryapp.data.remote.retrofit

import com.willyishmael.dicodingstoryapp.data.remote.response.GetStoriesResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.LoginResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.Response
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Response>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
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