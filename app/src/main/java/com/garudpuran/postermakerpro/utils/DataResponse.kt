package com.garudpuran.postermakerpro.utils

import androidx.annotation.Keep

@Keep
enum class Status {
    SUCCESS,
    ERROR,
    LOADING,
    SESSION_EXPIRE
}
public  data class Resource<out T>(val status: Status, val data: T?, val message: String?, val code: Int?) {
    companion object {
        fun <T> success(data: T?): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = null,null)

        fun <T> error(data: T?): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = null,null)

        fun <T> loading(data: T?): Resource<T> =
            Resource(status = Status.LOADING, data = data, message = null,null)

        fun <T> sessionExpire(message: String?, code: Int?): Resource<T> =
            Resource(status = Status.SESSION_EXPIRE, data = null, message , code)
    }
}

