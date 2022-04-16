package com.willyishmael.dicodingstoryapp.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.LoginResponse
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.willyishmael.dicodingstoryapp.data.remote.response.Response as ApiResponse


class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    fun register(name: String, email: String, password: String) {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", name)
            .addFormDataPart("email", email)
            .addFormDataPart("password", password)
            .build()

        val client = ApiConfig.getApiService().register(requestBody)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        TODO("Not yet implemented")
                    } else {
                        Log.d(TAG, response.message())
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse>,
                    t: Throwable
                ) {
                    Log.d(TAG, t.message.toString())
                }
            })
    }

    fun login(email: String, password: String) {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("email", email)
            .addFormDataPart("password", password)
            .build()

        val client = ApiConfig.getApiService().login(requestBody)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResult = response.body()?.loginResult
                    viewModelScope.launch {
                        pref.setLoginState(true)
                        pref.saveUserToken(loginResult?.token.toString())
                        pref.saveUserName(loginResult?.name.toString())
                    }
                } else {
                    TODO("Not yet implemented")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }
}