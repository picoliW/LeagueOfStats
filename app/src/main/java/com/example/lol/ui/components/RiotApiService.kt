package com.example.lol.ui.components

import com.example.lol.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

const val RIOT_API_KEY = BuildConfig.RIOT_API_KEY

fun provideOkHttpClient(): OkHttpClient {
    val interceptor = Interceptor { chain ->
        val original = chain.request()
        val originalUrl = original.url
        val url = originalUrl.newBuilder()
            .addQueryParameter("api_key", RIOT_API_KEY)
            .build()
        val request = original.newBuilder().url(url).build()
        chain.proceed(request)
    }

    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
}

fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://americas.api.riotgames.com")
        .client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideSummonerRetrofit(region: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://$region.api.riotgames.com")
        .client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

interface RiotAccountApi {
    @GET("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
    suspend fun getAccountByRiotId(
        @Path("gameName") gameName: String,
        @Path("tagLine") tagLine: String
    ): AccountResponse
}

interface RiotSummonerApi {
    @GET("/lol/summoner/v4/summoners/by-puuid/{encryptedPUUID}")
    suspend fun getSummonerByPUUID(
        @Path("encryptedPUUID") puuid: String
    ): SummonerResponse
}

data class AccountResponse(val puuid: String)
data class SummonerResponse(val summonerLevel: Int)

suspend fun getAccountPuuid(gameName: String, tagLine: String): String {
    val retrofit = provideRetrofit()
    val accountApi = retrofit.create(RiotAccountApi::class.java)
    return accountApi.getAccountByRiotId(gameName, tagLine).puuid
}

suspend fun getSummonerLevel(puuid: String, region: String): Int {
    val retrofit = provideSummonerRetrofit(region)
    val summonerApi = retrofit.create(RiotSummonerApi::class.java)
    return summonerApi.getSummonerByPUUID(puuid).summonerLevel
}
