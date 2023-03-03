package com.reflection.cryptoprices

import retrofit2.Response
import retrofit2.http.GET

interface CryptoApiService {
    @GET("assets")
    suspend fun getAssets(): Response<AssetsResponse>
}

data class AssetsResponse(
    val data: List<Coin>
)

data class Coin(
    val id: String,
    val rank: String,
    val symbol: String,
    val name: String,
    val priceUsd: String,
    val changePercent24Hr: String,
)