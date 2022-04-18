package com.willyishmael.dicodingstoryapp.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.LoginResponse
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    fun getLoginState() : LiveData<Boolean> {
        return pref.getLoginState().asLiveData()
    }

    fun login(email: String, password: String) {
        Log.e("response", "login")

        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.e("response", response.isSuccessful.toString())
                if (response.isSuccessful) {
                    val loginResult = response.body()?.loginResult
                    Log.e("response", response.body()?.loginResult?.token.toString())
                    viewModelScope.launch {
                        pref.setLoginState(true)
                        pref.saveUserToken(loginResult?.token.toString())
                        pref.saveUserName(loginResult?.name.toString())
                        Log.e("response", loginResult?.token.toString())
                    }
                } else {
                    Log.e(TAG, "Login - onResponse: ${response.message()}")
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