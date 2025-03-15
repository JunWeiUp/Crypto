package com.example.myapplication.data.repository

import com.example.myapplication.models.WalletState
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getWalletState(): Flow<WalletState>
    suspend fun refreshWalletState()
} 