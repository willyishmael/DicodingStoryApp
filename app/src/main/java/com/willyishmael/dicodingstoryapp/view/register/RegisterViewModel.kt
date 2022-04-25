package com.willyishmael.dicodingstoryapp.view.register

import android.util.Log
import androidx.lifecycle.*
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.LoginResponse
import com.willyishmael.dicodingstoryapp.data.remote.response.Response
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import com.willyishmael.dicodingstoryapp.utils.Loading
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback

class RegisterViewModel(private val pref: UserPreference) : ViewModel() {

    private var _isLoading = MutableLiveData<Loading>()
    val isLoading: LiveData<Loading> = _isLoading

    fun getLoginState() : LiveData<Boolean> {
        return pref.getLoginState().asLiveData()
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = Loading(true)

        ApiConfig.getApiService().register(name, email, password)
            .enqueue(object : Callback<Response> {
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    if (response.isSuccessful) {
                        _isLoading.value = Loading(
                            loadingState = false,
                            isLoadingSuccess = true,
                            response.message()
                        )
                        login(email, password)
                        Log.d(TAG, response.message())
                    } else {
                        _isLoading.value = Loading(
                            loadingState = false,
                            isLoadingSuccess = false,
                            response.message()
                        )
                    }
                }

                override fun onFailure(
                    call: Call<Response>,
                    t: Throwable
                ) {
                    _isLoading.value = Loading(
                        loadingState = false,
                        isLoadingSuccess = false,
                        "Register failed"
                    )
                    Log.d(TAG, t.message.toString())
                }
            })
    }

    private fun login(email: String, password: String) {
        _isLoading.value = Loading(true)

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
                    _isLoading.value = Loading(
                        loadingState = false,
                        isLoadingSuccess = true,
                        response.message()
                    )
                } else {
                    _isLoading.value = Loading(
                        loadingState = false,
                        isLoadingSuccess = false,
                        response.message()
                    )
                    Log.e(TAG, "Login - onResponse: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = Loading(
                    loadingState = false,
                    isLoadingSuccess = false,
                    "Login failed"
                )
                Log.e(TAG, "Login - onFailure: ${t.message.toString()}")
            }

        })
    }

    companion object {
        private val TAG = RegisterViewModel::class.java.simpleName
    }
}