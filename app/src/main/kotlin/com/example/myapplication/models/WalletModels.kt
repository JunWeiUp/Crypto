package com.example.myapplication.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 基础响应模型
@Serializable
data class BaseResponse(
    val ok: Boolean,
    val warning: String = ""
)

// 货币详细信息模型
@Serializable
data class CurrencyDetail(
    @SerialName("coin_id")
    val coinId: String,
    val name: String,
    val symbol: String,
    @SerialName("token_decimal")
    val tokenDecimal: Int,
    @SerialName("contract_address")
    val contractAddress: String = "",
    @SerialName("withdrawal_eta")
    val withdrawalEta: List<String> = emptyList(),
    @SerialName("colorful_image_url")
    val colorfulImageUrl: String = "",
    @SerialName("gray_image_url")
    val grayImageUrl: String = "",
    @SerialName("has_deposit_address_tag")
    val hasDepositAddressTag: Boolean = false,
    @SerialName("min_balance")
    val minBalance: Double = 0.0,
    @SerialName("blockchain_symbol")
    val blockchainSymbol: String = "",
    @SerialName("trading_symbol")
    val tradingSymbol: String = "",
    val code: String = "",
    val explorer: String = "",
    @SerialName("is_erc20")
    val isErc20: Boolean = false,
    @SerialName("gas_limit")
    val gasLimit: Int = 0,
    @SerialName("token_decimal_value")
    val tokenDecimalValue: String = "",
    @SerialName("display_decimal")
    val displayDecimal: Int = 0,
    @SerialName("supports_legacy_address")
    val supportsLegacyAddress: Boolean = false,
    @SerialName("deposit_address_tag_name")
    val depositAddressTagName: String = "",
    @SerialName("deposit_address_tag_type")
    val depositAddressTagType: String = "",
    @SerialName("num_confirmation_required")
    val numConfirmationRequired: Int = 0
)

// 支持的货币列表响应模型
@Serializable
data class SupportedCurrenciesResponse(
    val currencies: List<CurrencyDetail>,
    val total: Int = 0,
    val ok: Boolean = true,
    val warning: String = ""
)

// 汇率模型
@Serializable
data class Rate(
    val amount: String,
    val rate: String
)

@Serializable
data class CurrencyRate(
    @SerialName("from_currency")
    val fromCurrency: String,
    @SerialName("to_currency")
    val toCurrency: String,
    val rates: List<Rate>,
    @SerialName("time_stamp")
    val timeStamp: Long
)

@Serializable
data class LiveRatesResponse(
    val ok: Boolean,
    val warning: String = "",
    val tiers: List<CurrencyRate>
)

// 钱包余额模型
@Serializable
data class WalletBalance(
    val currency: String,
    val amount: Double
)

@Serializable
data class WalletBalanceResponse(
    val ok: Boolean,
    val warning: String = "",
    val wallet: List<WalletBalance>
)

// 聚合模型 - 用于展示
data class CurrencyInfo(
    val symbol: String,
    val name: String,
    val balance: Double,
    val rate: Double,
    val timestamp: Long,
    val tokenDecimal: Int,
    val isErc20: Boolean,
    val contractAddress: String,
    val colorfulImageUrl: String
) {
    val usdValue: Double
        get() = balance * rate
}

// 钱包状态模型
data class WalletState(
    val currencies: List<CurrencyInfo>,
    val totalUsdValue: Double = currencies.sumOf { it.usdValue },
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        val SUPPORTED_CURRENCIES = setOf("BTC", "ETH", "CRO", "USDT", "DAI")
    }
} 