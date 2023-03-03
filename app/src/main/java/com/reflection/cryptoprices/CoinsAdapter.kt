package com.reflection.cryptoprices
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reflection.cryptoprices.databinding.CoinDetailsItemBinding

class CoinsAdapter (
    private val coins : MutableList<Coin>
    ) : RecyclerView.Adapter<CoinsAdapter.CoinsViewHolder>() {

    class CoinsViewHolder(val binding: CoinDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsViewHolder {
        val binding =
            CoinDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinsViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CoinsViewHolder, position: Int) {
        val curCoin = coins[position]
        holder.binding.tvRank.text = "Rank: " + curCoin.rank
        holder.binding.tvCoinName.text = "Name: " + curCoin.name
        holder.binding.tvCoinSymbol.text = "Symbol: " + curCoin.symbol
        holder.binding.tvCoinPrice.text = "Price (in USD): $" + String.format("%.4f", curCoin.priceUsd.toDouble())
        holder.binding.tvChangePercent24Hr.text = "Price change percent 24h: " + String.format("%.3f", curCoin.changePercent24Hr.toDouble()) + "%"

    }

    override fun getItemCount(): Int {
        return coins.size
    }

    fun addCoinItem(coin: Coin) {
        coins.add(coin)
        notifyItemInserted(coins.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAllCoins() {
        coins.clear()
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged()
        }
    }

}