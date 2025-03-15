package com.example.myapplication.data.source

import android.content.Context
import com.example.myapplication.models.WalletState

interface WalletDataSource {
    suspend fun getWalletState(context: Context): WalletState
} 