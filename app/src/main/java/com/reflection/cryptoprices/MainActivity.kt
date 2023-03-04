package com.reflection.cryptoprices

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.reflection.cryptoprices.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private val baseUrl = "https://api.coincap.io/v2/"
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var coinsAdapter: CoinsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        coinsAdapter = CoinsAdapter(mutableListOf())
        binding.rvCoinsItems.adapter = coinsAdapter
        binding.rvCoinsItems.layoutManager = LinearLayoutManager(this)


        // Create OkHttpClient with logging interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // Create service interface
        val service = retrofit.create(CryptoApiService::class.java)
        val timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                updateCryptoPrices(service)
            }
        }, 0L, 10 * 60 * 1000L) // Delay 0 milliseconds, repeat every 10 minutes (in milliseconds)


    }


    private fun updateCryptoPrices(service : CryptoApiService)
    {
        coinsAdapter.deleteAllCoins()
        coroutineScope.launch {
            // Make GET request on background thread and receive response on main thread
            val response = withContext(Dispatchers.IO) { service.getAssets() }
            if (response.isSuccessful) {
                // Handle successful response
                val assetsResponse = response.body()
                if (assetsResponse != null) {
                    val assets = assetsResponse.data


                    // Parsing response
                    for (asset in assets) {
                        coinsAdapter.addCoinItem(
                            Coin(
                                asset.id,
                                asset.rank,
                                asset.symbol,
                                asset.name,
                                asset.priceUsd,
                                asset.changePercent24Hr,
                            )
                        )
                    }
                    //println(assetsList)
                }
            } else {
                // Handle error response
                val errorMessage = response.errorBody()?.string()
                println(errorMessage)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
