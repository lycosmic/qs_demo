package com.example.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * 统一响应体
 */
data class ApiResponse<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("msg") val message: String,
    @SerializedName("data") val data: T?
) {
    // 判断业务是否成功
    fun isSuccess(): Boolean = code == 200
}