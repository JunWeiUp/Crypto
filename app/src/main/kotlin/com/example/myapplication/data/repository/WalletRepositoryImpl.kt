package com.example.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.data.source.WalletDataSource
import com.example.myapplication.models.WalletState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val context: Context,
    private val dataSource: WalletDataSource
) : WalletRepository {

    private val TAG = "WalletRepositoryImpl"

    override fun getWalletState(): Flow<WalletState> = flow {
        try {
            Log.d(TAG, "开始获取钱包状态")
            val state = dataSource.getWalletState(context)
            Log.d(TAG, "获取钱包状态成功: ${state.currencies.size} 个货币")
            emit(state)
        } catch (e: Exception) {
            Log.e(TAG, "获取钱包状态失败: ${e.message}", e)
            throw e
        }
    }

    override suspend fun refreshWalletState() {
        // 实现刷新逻辑，可以在这里添加缓存刷新等
        Log.d(TAG, "刷新钱包状态")
    }
} 