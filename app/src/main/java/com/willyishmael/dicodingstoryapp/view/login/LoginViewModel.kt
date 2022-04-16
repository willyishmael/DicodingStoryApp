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

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

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
                    Log.e(TAG, "Login - onResponse: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "Login - onFailure: ${t.message.toString()}")
            }

        })
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }
}