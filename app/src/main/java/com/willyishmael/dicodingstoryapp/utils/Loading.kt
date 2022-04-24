package com.willyishmael.dicodingstoryapp.utils

data class Loading(
    val loadingState: Boolean,
    val isLoadingSuccess: Boolean = false,
    val message: String = ""
)
