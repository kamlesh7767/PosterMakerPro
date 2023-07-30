package com.garudpuran.postermakerpro.utils

sealed class UiState<out T> {
    //loading,success,failure

    object Loading: UiState<Nothing>()
    data class Success<out T>(val data: T): UiState<T>()
    data class Failure(val error: String?): UiState<Nothing>()

}