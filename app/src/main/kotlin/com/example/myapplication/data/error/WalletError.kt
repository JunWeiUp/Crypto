package com.example.myapplication.data.error

sealed class WalletError : Exception() {
    data class FileNotFound(override val message: String) : WalletError()
    data class ParseError(override val message: String) : WalletError()
    data class InvalidData(override val message: String) : WalletError()
    data class NetworkError(override val message: String) : WalletError()
    data class UnknownError(override val message: String) : WalletError()
} 