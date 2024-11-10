package com.example.lol.repository

import com.example.lol.data.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RiotRepository(
    private val riotAccountApi: RiotAccountApi,
    private val riotSummonerApi: RiotSummonerApi,
    private val riotChampionMasteryApi: RiotChampionMasteryApi,
    private val riotMatchApi: RiotMatchApi
) {

    suspend fun getAccountPuuid(gameName: String, tagLine: String): String = withContext(Dispatchers.IO) {
        riotAccountApi.getAccountByRiotId(gameName, tagLine).puuid
    }

    suspend fun getSummonerLevel(puuid: String): Int = withContext(Dispatchers.IO) {
        riotSummonerApi.getSummonerByPUUID(puuid).summonerLevel
    }

    suspend fun getTopChampionMasteries(puuid: String): List<ChampionMasteryResponse> = withContext(Dispatchers.IO) {
        riotChampionMasteryApi.getTopChampionMasteries(puuid)
    }

    suspend fun getMatchIds(puuid: String, start: Int = 0, count: Int = 100): List<String> = withContext(Dispatchers.IO) {
        riotMatchApi.getMatchIds(puuid, start, count)
    }

    suspend fun getMatchDetails(matchId: String): MatchDetailsResponse = withContext(Dispatchers.IO) {
        riotMatchApi.getMatchDetails(matchId)
    }

    suspend fun getMatchDetailsWithSummonerNames(matchId: String): Pair<MatchDetailsResponse, List<String>> {
        val matchDetails = getMatchDetails(matchId)
        val summonerNames = getSummonerNamesByPUUIDs(matchDetails.metadata.participants)
        return matchDetails to summonerNames
    }

    suspend fun getSummonerNamesByPUUIDs(participants: List<String>): List<String> {
        return participants.map { puuid ->
            val account = riotAccountApi.getAccountByPUUID(puuid)
            "${account.gameName}#${account.tagLine}"
        }
    }

    suspend fun getMatchDetailsWithSummonerNamesAndChampions(matchId: String): List<ParticipantData> {
        val matchDetails = getMatchDetails(matchId)
        return matchDetails.info.participants.map { participant ->
            ParticipantData(
                riotIdGameName = participant.riotIdGameName,
                championId = participant.championId,
                championName = participant.championName,
                individualPosition = participant.individualPosition,
                teamId = participant.teamId,
                kills = participant.kills,
                deaths = participant.deaths,
                assists = participant.assists,
                champLevel = participant.champLevel,
            )
        }
    }
}
