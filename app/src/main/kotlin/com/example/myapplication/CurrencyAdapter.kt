package com.example.myapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.models.CurrencyInfo
import java.text.NumberFormat
import java.util.Locale

class CurrencyAdapter : ListAdapter<CurrencyInfo, CurrencyAdapter.ViewHolder>(CurrencyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val currencyIcon: ImageView = itemView.findViewById(R.id.currencyIcon)
        private val currencyName: TextView = itemView.findViewById(R.id.currencyName)
        private val currencyBalance: TextView = itemView.findViewById(R.id.currencyBalance)
        private val usdBalance: TextView = itemView.findViewById(R.id.usdBalance)

        fun bind(currency: CurrencyInfo) {
            val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 8
            }
            
            // 设置货币图标
            loadCurrencyIcon(currency.colorfulImageUrl)
            
            // 设置货币名称
            currencyName.text = currency.name
            
            // 设置货币余额
            currencyBalance.text = "${numberFormat.format(currency.balance)} ${currency.symbol}"
            
            // 设置美元余额
            usdBalance.text = "$${numberFormat.format(currency.usdValue)}"
            
        }

        private fun loadCurrencyIcon(imageUrl: String) {
            if (imageUrl.isNotEmpty()) {
                Log.d("CurrencyAdapter", "开始加载图片: $imageUrl")
                
                // 使用 HTTPS 替代 HTTP
                val secureUrl = if (imageUrl.startsWith("http://")) {
                    imageUrl.replace("http://", "https://")
                } else {
                    imageUrl
                }
                
                currencyIcon.load(secureUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_currency_placeholder)
                    error(R.drawable.ic_currency_placeholder)
                    transformations(CircleCropTransformation())
                    size(40, 40)
                    memoryCacheKey("currency_$secureUrl")
                    listener(
                        onStart = { 
                            Log.d("CurrencyAdapter", "开始加载图片: $secureUrl") 
                        },
                        onSuccess = { _, _ -> 
                            Log.d("CurrencyAdapter", "图片加载成功: $secureUrl") 
                        },
                        onError = { _, e -> 
                            Log.e("CurrencyAdapter", "图片加载失败: $secureUrl")
                        }
                    )
                }
            } else {
                currencyIcon.setImageResource(R.drawable.ic_currency_placeholder)
            }
        }
    }
}

class CurrencyDiffCallback : DiffUtil.ItemCallback<CurrencyInfo>() {
    override fun areItemsTheSame(oldItem: CurrencyInfo, newItem: CurrencyInfo): Boolean {
        return oldItem.symbol == newItem.symbol
    }

    override fun areContentsTheSame(oldItem: CurrencyInfo, newItem: CurrencyInfo): Boolean {
        return oldItem == newItem
    }
} 