package com.willyishmael.dicodingstoryapp.view.login

import android.util.Log
import androidx.lifecycle.*
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.LoginResponse
import com.willyishmael.dicodingstoryapp.data.remote.retrofit.ApiConfig
import com.willyishmael.dicodingstoryapp.utils.Loading
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private var _isLoading = MutableLiveData<Loading>()
    val isLoading: LiveData<Loading> = _isLoading

    fun getLoginState() : LiveData<Boolean> {
        return pref.getLoginState().asLiveData()
    }

    fun login(email: String, password: String) {
        _isLoading.value = Loading(true)

        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
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
                    Log.d(TAG, "Login - onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = Loading(
                    loadingState = false,
                    isLoadingSuccess = true,
                    "Login failed"
                )
                Log.e(TAG, "Login - onFailure: ${t.message.toString()}")
            }

        })
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }
}