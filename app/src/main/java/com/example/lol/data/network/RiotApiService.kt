package com.example.lol.data.network

import com.example.lol.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val RIOT_API_KEY = BuildConfig.RIOT_API_KEY


data class AccountResponse(
    val puuid: String,
    val gameName: String,
    val tagLine: String
)

data class SummonerResponse(
    val id: String,
    val accountId: String,
    val puuid: String,
    val profileIconId: String,
    val revisionDate: String,
    val summonerLevel: Int
)

data class ChampionMasteryResponse(
    val championId: Int,
    val championLevel: Int,
    val championPoints: Int,
    val lastPlayTime: Long
)


data class MatchDetailsResponse(
    val metadata: Metadata,
    val info: Info
)

data class Metadata(
    val matchId: String,
    val participants: List<String>
)

data class Info(
    val gameDuration: Int,
    val gameMode: String,
    val participants: List<Participant>
)

data class Participant(
    val puuid: String,
    val riotIdGameName: String,
    val riotIdTagLine: String,
    val championName: String,
    val individualPosition: String,
    val teamId: String,
    val kills: String,
    val deaths: String,
    val assists: String,
    val champLevel: String,
)

data class ParticipantData(
    val riotIdGameName: String,
    val championName: String,
    val individualPosition: String,
    val teamId: String,
    val kills: String,
    val deaths: String,
    val assists: String,
    val champLevel: String,
)

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

    @GET("/riot/account/v1/accounts/by-puuid/{puuid}")
    suspend fun getAccountByPUUID(
        @Path("puuid") puuid: String
    ): AccountResponse
}

interface RiotSummonerApi {
    @GET("/lol/summoner/v4/summoners/by-puuid/{encryptedPUUID}")
    suspend fun getSummonerByPUUID(
        @Path("encryptedPUUID") puuid: String
    ): SummonerResponse
}

interface RiotChampionMasteryApi {
    @GET("/lol/champion-mastery/v4/champion-masteries/by-puuid/{puuid}/top?count=5")
    suspend fun getTopChampionMasteries(
        @Path("puuid") puuid: String
    ): List<ChampionMasteryResponse>
}



interface RiotMatchApi {
    @GET("/lol/match/v5/matches/by-puuid/{puuid}/ids")
    suspend fun getMatchIds(
        @Path("puuid") puuid: String,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 100
    ): List<String>

    @GET("/lol/match/v5/matches/{matchId}")
    suspend fun getMatchDetails(
        @Path("matchId") matchId: String
    ): MatchDetailsResponse
}






