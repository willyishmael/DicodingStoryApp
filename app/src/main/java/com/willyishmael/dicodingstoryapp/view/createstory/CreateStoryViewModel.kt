package com.willyishmael.dicodingstoryapp.view.createstory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.Response
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import com.willyishmael.dicodingstoryapp.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File

class CreateStoryViewModel(private val pref: UserPreference) : ViewModel() {

    fun getUserToken() = pref.getCurrentUserToken().asLiveData()

    fun getLoginState() = pref.getLoginState().asLiveData()

    fun uploadStory(token: String, file: File, description: String) {

        val bearerToken = "Bearer $token"

        val reducedImage = reduceFileImage(file)
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            reducedImage.name,
            reducedImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        val reqDescription = description.toRequestBody("text/plain".toMediaType())

        val client = ApiConfig
            .getApiService()
            .createStories(bearerToken, imageMultipart, reqDescription)

        client.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        Log.d(TAG, "Upload Story - onResponse ${response.message()}")
                    }
                } else {
                    Log.d(TAG, "Upload Story - onResponse ${response.message()}")
                }

            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.d(TAG, "Upload Story - onFailure ${t.message}")
            }

        })
    }

    companion object {
        private val TAG = CreateStoryViewModel::class.java.simpleName
    }
}