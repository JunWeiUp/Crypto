package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.error.WalletError
import com.example.myapplication.data.repository.WalletRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshButton: Button
    private lateinit var totalAssetsValue: TextView
    private lateinit var lastUpdatedTime: TextView
    
    @Inject
    lateinit var walletRepository: WalletRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")

        setupViews()
        loadWalletState()
    }

    private fun setupViews() {
        Log.d(TAG, "setupViews")
        // 设置 RecyclerView
        recyclerView = findViewById(R.id.currencyRecyclerView)
        currencyAdapter = CurrencyAdapter()
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = currencyAdapter
        }
        
        // 设置总资产视图
        totalAssetsValue = findViewById(R.id.totalAssetsValue)
        lastUpdatedTime = findViewById(R.id.lastUpdatedTime)
        
        // 设置刷新按钮
        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            Log.d(TAG, "刷新按钮点击")
            loadWalletState()
        }
    }

    private fun loadWalletState() {
        Log.d(TAG, "开始加载钱包状态")
        lifecycleScope.launch {
            Log.d(TAG, "在协程中加载钱包状态")
            walletRepository.getWalletState()
                .catch { e ->
                    Log.e(TAG, "加载钱包状态失败: ${e.message}", e)
                    val errorMessage = when (e) {
                        is WalletError.FileNotFound -> "找不到钱包数据文件: ${e.message}"
                        is WalletError.ParseError -> "解析钱包数据失败: ${e.message}"
                        is WalletError.InvalidData -> "钱包数据无效: ${e.message}"
                        is WalletError.NetworkError -> "网络连接失败: ${e.message}"
                        else -> "未知错误: ${e.message}"
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "错误: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .collect { state ->
                    Log.d(TAG, "钱包状态加载成功，货币数量: ${state.currencies.size}")
                    
                    // 更新货币列表
                    currencyAdapter.submitList(state.currencies)
                    
                    // 更新总资产
                    updateTotalAssets(state.totalUsdValue, state.lastUpdated)
                }
        }
    }
    
    private fun updateTotalAssets(totalUsdValue: Double, lastUpdated: Long) {
        // 格式化总资产金额
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
        totalAssetsValue.text = numberFormat.format(totalUsdValue)
        
        // 格式化最后更新时间
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        lastUpdatedTime.text = dateFormat.format(Date(lastUpdated))
    }
} 