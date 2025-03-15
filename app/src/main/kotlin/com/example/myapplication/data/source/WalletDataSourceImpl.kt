package com.example.myapplication.data.source

import android.content.Context
import android.util.Log
import com.example.myapplication.data.error.WalletError
import com.example.myapplication.models.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

class WalletDataSourceImpl @Inject constructor(
    private val json: Json
) : WalletDataSource {

    private val TAG = "WalletDataSourceImpl"

    private fun loadJSONFromAsset(context: Context, fileName: String): String {
        return try {
            Log.d(TAG, "开始加载 $fileName")
            context.assets.open(fileName).use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            }.also {
                Log.d(TAG, "$fileName 加载成功，长度: ${it.length}")
                // 打印前100个字符，帮助调试
                Log.d(TAG, "$fileName 内容预览: ${it.take(100)}...")
            }
        } catch (ex: IOException) {
            Log.e(TAG, "加载 $fileName 失败: ${ex.message}", ex)
            throw WalletError.FileNotFound("Failed to read $fileName: ${ex.message}")
        }
    }

    override suspend fun getWalletState(context: Context): WalletState {
        try {
            Log.d(TAG, "开始获取钱包状态")
            
            // 读取JSON数据
            val currenciesJson = loadJSONFromAsset(context, "currencies.json")
            val ratesJson = loadJSONFromAsset(context, "live-rates.json")
            val balancesJson = loadJSONFromAsset(context, "wallet-balance.json")

            // 解析JSON数据
            Log.d(TAG, "开始解析 JSON 数据")
            
            val currenciesResponse = try {
                json.decodeFromString<SupportedCurrenciesResponse>(currenciesJson)
            } catch (e: SerializationException) {
                Log.e(TAG, "解析 currencies.json 失败: ${e.message}", e)
                throw WalletError.ParseError("Failed to parse currencies.json: ${e.message}")
            }
            Log.d(TAG, "currencies.json 解析成功，货币数量: ${currenciesResponse.currencies.size}")
            
            val ratesResponse = try {
                json.decodeFromString<LiveRatesResponse>(ratesJson)
            } catch (e: SerializationException) {
                Log.e(TAG, "解析 live-rates.json 失败: ${e.message}", e)
                throw WalletError.ParseError("Failed to parse live-rates.json: ${e.message}")
            }
            Log.d(TAG, "live-rates.json 解析成功，汇率数量: ${ratesResponse.tiers.size}")
            
            val balanceResponse = try {
                json.decodeFromString<WalletBalanceResponse>(balancesJson)
            } catch (e: SerializationException) {
                Log.e(TAG, "解析 wallet-balance.json 失败: ${e.message}", e)
                throw WalletError.ParseError("Failed to parse wallet-balance.json: ${e.message}")
            }
            Log.d(TAG, "wallet-balance.json 解析成功，余额数量: ${balanceResponse.wallet.size}")

            // 获取支持的货币列表（与预定义的支持货币取交集）
            val supportedCurrencies = currenciesResponse.currencies
                .filter { it.symbol in WalletState.SUPPORTED_CURRENCIES }
                .associate { it.symbol to it }
            Log.d(TAG, "支持的货币: ${supportedCurrencies.keys}")

            // 提取USD汇率
            val usdRates = ratesResponse.tiers
                .filter { it.toCurrency == "USD" && it.fromCurrency in supportedCurrencies.keys }
                .associate { rate -> 
                    rate.fromCurrency to (rate.rates.firstOrNull()?.rate?.toDoubleOrNull() ?: 0.0)
                }
            Log.d(TAG, "USD 汇率: $usdRates")

            // 提取余额
            val balances = balanceResponse.wallet
                .filter { it.currency in supportedCurrencies.keys }
                .associate { it.currency to it.amount }
            Log.d(TAG, "余额: $balances")

            // 验证数据
            validateData(supportedCurrencies.keys, usdRates)

            // 构建货币信息列表
            val currencyInfoList = supportedCurrencies.map { (symbol, detail) ->
                CurrencyInfo(
                    symbol = symbol,
                    name = detail.name,
                    balance = balances[symbol] ?: 0.0,
                    rate = usdRates[symbol] ?: throw WalletError.InvalidData("Missing rate for $symbol"),
                    timestamp = ratesResponse.tiers.firstOrNull { it.fromCurrency == symbol }?.timeStamp ?: 0,
                    tokenDecimal = detail.tokenDecimal,
                    isErc20 = detail.isErc20,
                    contractAddress = detail.contractAddress,
                    colorfulImageUrl = detail.colorfulImageUrl
                )
            }.sortedBy { it.symbol }
            Log.d(TAG, "构建的货币信息列表大小: ${currencyInfoList.size}")

            // 返回钱包状态
            return WalletState(currencies = currencyInfoList).also {
                Log.d(TAG, "钱包状态获取成功，总价值: ${it.totalUsdValue}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取钱包状态失败: ${e.message}", e)
            val error = when (e) {
                is WalletError -> e
                is SerializationException -> 
                    WalletError.ParseError("Failed to parse JSON data: ${e.message}")
                else -> WalletError.InvalidData("Unexpected error: ${e.message}")
            }
            throw error
        }
    }

    private fun validateData(supportedCurrencies: Set<String>, rates: Map<String, Double>) {
        // 验证是否有支持的货币
        if (supportedCurrencies.isEmpty()) {
            Log.e(TAG, "没有支持的货币")
            throw WalletError.InvalidData("No supported currencies found")
        }

        // 验证所有支持的货币都有有效的汇率
        supportedCurrencies.forEach { currency ->
            if (!rates.containsKey(currency)) {
                Log.e(TAG, "缺少 $currency 的汇率")
                throw WalletError.InvalidData("Missing exchange rate for $currency")
            }
            if (rates[currency] == 0.0) {
                Log.e(TAG, "$currency 的汇率无效 (0.0)")
                throw WalletError.InvalidData("Invalid exchange rate (0.0) for $currency")
            }
        }
        Log.d(TAG, "数据验证通过")
    }
} 