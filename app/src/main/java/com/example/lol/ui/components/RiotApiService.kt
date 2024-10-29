package com.example.lol.ui.components

import android.util.Log
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

interface RiotChampionMasteryApi {
    @GET("/lol/champion-mastery/v4/champion-masteries/by-puuid/{puuid}/top?count=5")
    suspend fun getTopChampionMasteries(
        @Path("puuid") puuid: String
    ): List<ChampionMasteryResponse>
}

data class ChampionMasteryResponse(
    val championId: Int,
    val championLevel: Int,
    val championPoints: Int,
    val lastPlayTime: Long
)

suspend fun getTopChampionMasteries(puuid: String, region: String): List<ChampionMasteryResponse> {
    val retrofit = provideSummonerRetrofit(region)
    val masteryApi = retrofit.create(RiotChampionMasteryApi::class.java)
    return masteryApi.getTopChampionMasteries(puuid)
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
)

data class ParticipantData(
    val riotIdGameName: String,
    val championName: String,
    val individualPosition: String,
    val teamId: String,
    val kills: String,
    val deaths: String,
    val assists: String,
)


suspend fun getMatchIds(puuid: String, region: String): List<String> {
    val retrofit = provideSummonerRetrofit(region)
    val matchApi = retrofit.create(RiotMatchApi::class.java)
    return matchApi.getMatchIds(puuid)
}

suspend fun getMatchDetails(matchId: String, region: String): MatchDetailsResponse {
    val retrofit = provideSummonerRetrofit(region)
    val matchApi = retrofit.create(RiotMatchApi::class.java)
    return matchApi.getMatchDetails(matchId)
}

suspend fun getMatchDetailsWithSummonerNames(matchId: String, region: String): Pair<MatchDetailsResponse, List<String>> {
    val matchDetails = getMatchDetails(matchId, region)
    val summonerNames = getSummonerNamesByPUUIDs(matchDetails.metadata.participants, region)
    return matchDetails to summonerNames
}

suspend fun getSummonerNamesByPUUIDs(participants: List<String>, region: String): List<String> {
    val retrofit = provideSummonerRetrofit(region)
    val accountApi = retrofit.create(RiotAccountApi::class.java)
    val summonerNames = mutableListOf<String>()

    for (puuid in participants) {
        val account = accountApi.getAccountByPUUID(puuid)
        val summonerName = "${account.gameName}#${account.tagLine}"
        summonerNames.add(summonerName)
    }

    return summonerNames }

suspend fun getMatchDetailsWithSummonerNamesAndChampions(matchId: String, region: String): List<ParticipantData> {
    val matchDetails = getMatchDetails(matchId, region)
    return matchDetails.info.participants.map { participant ->
        ParticipantData(
            riotIdGameName = participant.riotIdGameName,
            championName = participant.championName,
            individualPosition = participant.individualPosition,
            teamId = participant.teamId,
            kills = participant.kills,
            deaths = participant.deaths,
            assists = participant.assists
        )
    }
}






