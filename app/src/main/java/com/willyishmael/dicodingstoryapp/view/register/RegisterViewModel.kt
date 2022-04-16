package com.willyishmael.dicodingstoryapp.view.register

import android.util.Log
import androidx.lifecycle.ViewModel
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.Response
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class RegisterViewModel(private val pref: UserPreference) : ViewModel() {
    fun register(name: String, email: String, password: String) {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", name)
            .addFormDataPart("email", email)
            .addFormDataPart("password", password)
            .build()

        val client = ApiConfig.getApiService().register(requestBody)
            .enqueue(object : Callback<Response> {
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    if (response.isSuccessful) {
                        TODO("Not yet implemented")
                    } else {
                        Log.d(TAG, response.message())
                    }
                }

                override fun onFailure(
                    call: Call<Response>,
                    t: Throwable
                ) {
                    Log.d(TAG, t.message.toString())
                }
            })
    }

    companion object {
        private val TAG = RegisterViewModel::class.java.simpleName
    }
}