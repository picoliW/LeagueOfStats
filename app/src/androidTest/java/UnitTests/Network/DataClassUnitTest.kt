import com.example.lol.data.network.*
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.Retrofit

class DataClassUnitTest {

    private val gson = Gson()

    @Test
    fun testAccountResponseDeserialization() {
        val json = """
            {
                "puuid": "1234abcd",
                "gameName": "PlayerOne",
                "tagLine": "BR1"
            }
        """
        val accountResponse = gson.fromJson(json, AccountResponse::class.java)
        assertEquals("1234abcd", accountResponse.puuid)
        assertEquals("PlayerOne", accountResponse.gameName)
        assertEquals("BR1", accountResponse.tagLine)
    }

    @Test
    fun testSummonerResponseDeserialization() {
        val json = """
            {
                "id": "abcd1234",
                "accountId": "account1234",
                "puuid": "1234abcd",
                "profileIconId": "789",
                "revisionDate": "1616196498000",
                "summonerLevel": 30
            }
        """
        val summonerResponse = gson.fromJson(json, SummonerResponse::class.java)
        assertEquals("abcd1234", summonerResponse.id)
        assertEquals("account1234", summonerResponse.accountId)
        assertEquals("1234abcd", summonerResponse.puuid)
        assertEquals("789", summonerResponse.profileIconId)
        assertEquals("1616196498000", summonerResponse.revisionDate)
        assertEquals(30, summonerResponse.summonerLevel)
    }

    @Test
    fun testChampionMasteryResponseDeserialization() {
        val json = """
            {
                "championId": 1,
                "championLevel": 7,
                "championPoints": 50000,
                "lastPlayTime": 1616196498000
            }
        """
        val masteryResponse = gson.fromJson(json, ChampionMasteryResponse::class.java)
        assertEquals(1, masteryResponse.championId)
        assertEquals(7, masteryResponse.championLevel)
        assertEquals(50000, masteryResponse.championPoints)
        assertEquals(1616196498000, masteryResponse.lastPlayTime)
    }

    @Test
    fun testMatchDetailsResponseDeserialization() {
        val json = """
            {
                "metadata": {
                    "matchId": "BR1_123456",
                    "participants": ["puuid1", "puuid2"]
                },
                "info": {
                    "gameDuration": 1800,
                    "gameMode": "CLASSIC",
                    "participants": [
                        {
                            "puuid": "puuid1",
                            "riotIdGameName": "PlayerOne",
                            "championId": 101,
                            "riotIdTagLine": "BR1",
                            "championName": "ChampionOne",
                            "individualPosition": "MIDDLE",
                            "teamId": "100",
                            "kills": "10",
                            "deaths": "2",
                            "assists": "8",
                            "champLevel": "18"
                        }
                    ]
                }
            }
        """
        val matchDetails = gson.fromJson(json, MatchDetailsResponse::class.java)
        assertEquals("BR1_123456", matchDetails.metadata.matchId)
        assertEquals(2, matchDetails.metadata.participants.size)
        assertEquals("puuid1", matchDetails.metadata.participants[0])
        assertEquals(1800, matchDetails.info.gameDuration)
        assertEquals("CLASSIC", matchDetails.info.gameMode)
        assertEquals(1, matchDetails.info.participants.size)
        assertEquals("PlayerOne", matchDetails.info.participants[0].riotIdGameName)
        assertEquals(101, matchDetails.info.participants[0].championId)
    }

    @Test
    fun testParticipantDeserialization() {
        val json = """
            {
                "riotIdGameName": "PlayerOne",
                "championId": 101,
                "riotIdTagLine": "#penis",
                "championName": "Aatrox",
                "individualPosition": "MIDDLE",
                "teamId": "100",
                "kills": "2",
                "deaths": "8",
                "assists": "18",
                "champLevel": "18"
            }
        """
        val participant = gson.fromJson(json, Participant::class.java)
        assertEquals("PlayerOne", participant.riotIdGameName)
        assertEquals(101, participant.championId)
        assertEquals("#penis", participant.riotIdTagLine)
        assertEquals("Aatrox", participant.championName)
        assertEquals("MIDDLE", participant.individualPosition)
        assertEquals("100", participant.teamId)
        assertEquals("2", participant.kills)
        assertEquals("8", participant.deaths)
        assertEquals("18", participant.assists)
        assertEquals("18", participant.champLevel)
    }

    @Test
    fun testParticipantDataDeserialization() {
        val json = """
            {
                "riotIdGameName": "PlayerOne",
                "championId": 101,
                "championName": "ChampionOne",
                "individualPosition": "MIDDLE",
                "teamId": "100",
                "kills": "10",
                "deaths": "2",
                "assists": "8",
                "champLevel": "18"
            }
        """
        val participantData = gson.fromJson(json, ParticipantData::class.java)
        assertEquals("PlayerOne", participantData.riotIdGameName)
        assertEquals(101, participantData.championId)
        assertEquals("ChampionOne", participantData.championName)
        assertEquals("MIDDLE", participantData.individualPosition)
        assertEquals("100", participantData.teamId)
        assertEquals("10", participantData.kills)
        assertEquals("2", participantData.deaths)
        assertEquals("8", participantData.assists)
        assertEquals("18", participantData.champLevel)
    }

    @Test
    fun testProvideOkHttpClient() {
        val okHttpClient = provideOkHttpClient()
        assertNotNull(okHttpClient)
        assertEquals(2, okHttpClient.interceptors.size)
    }

    @Test
    fun testProvideRetrofit() {
        val retrofit = provideRetrofit()
        assertNotNull(retrofit)
        assertEquals("https://americas.api.riotgames.com/", retrofit.baseUrl().toString())
    }

    @Test
    fun testProvideSummonerRetrofit() {
        val region = "na1"
        val summonerRetrofit = provideSummonerRetrofit(region)
        assertNotNull(summonerRetrofit)
        assertEquals("https://$region.api.riotgames.com/", summonerRetrofit.baseUrl().toString())
    }
}
