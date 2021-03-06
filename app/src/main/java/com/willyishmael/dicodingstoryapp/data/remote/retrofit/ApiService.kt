package com.willyishmael.dicodingstoryapp.data.remote.retrofit

import com.willyishmael.dicodingstoryapp.data.remote.response.GetStoriesResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.LoginResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.Response
import okhttp3.MultipartBody
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

    @Multipart
    @POST("stories")
    fun createStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<Response>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = 0,
        @Query("size") size: Int? = 0,
        @Query("location") location: Int? = 0
    ): GetStoriesResponse

    @GET("stories")
    fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = 0,
        @Query("size") size: Int? = 10,
        @Query("location") location: Int? = 1
    ): Call<GetStoriesResponse>
}