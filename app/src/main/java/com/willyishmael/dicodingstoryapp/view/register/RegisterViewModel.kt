package com.willyishmael.dicodingstoryapp.view.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.LoginResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.Response
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class RegisterViewModel(private val pref: UserPreference) : ViewModel() {

    fun getLoginState() : LiveData<Boolean> {
        return pref.getLoginState().asLiveData()
    }

    fun register(name: String, email: String, password: String) {

        ApiConfig.getApiService().register(name, email, password)
            .enqueue(object : Callback<Response> {
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    val isError = response.body()?.error
                    val message = response.body()?.message.toString()
                    if (isError == false) {
                        login(email, password)
                    }

                    Log.d(TAG, message)
                }

                override fun onFailure(
                    call: Call<Response>,
                    t: Throwable
                ) {
                    Log.d(TAG, t.message.toString())
                }
            })
    }

    private fun login(email: String, password: String) {

        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
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
        private val TAG = RegisterViewModel::class.java.simpleName
    }
}