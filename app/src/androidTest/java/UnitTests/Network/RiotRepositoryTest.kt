import com.example.lol.data.network.*
import com.example.lol.repository.RiotRepository
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RiotRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var riotRepository: RiotRepository

    private lateinit var riotAccountApi: RiotAccountApi
    private lateinit var riotSummonerApi: RiotSummonerApi
    private lateinit var riotChampionMasteryApi: RiotChampionMasteryApi
    private lateinit var riotMatchApi: RiotMatchApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString()

        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        riotAccountApi = retrofit.create(RiotAccountApi::class.java)
        riotSummonerApi = retrofit.create(RiotSummonerApi::class.java)
        riotChampionMasteryApi = retrofit.create(RiotChampionMasteryApi::class.java)
        riotMatchApi = retrofit.create(RiotMatchApi::class.java)

        riotRepository = RiotRepository(
            riotAccountApi = riotAccountApi,
            riotSummonerApi = riotSummonerApi,
            riotChampionMasteryApi = riotChampionMasteryApi,
            riotMatchApi = riotMatchApi
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getAccountPuuidTest() = runBlocking {
        val mockResponse = MockResponse()
            .setBody("""{"puuid": "1234abcd", "gameName": "PlayerOne", "tagLine": "BR1"}""")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val puuid = riotRepository.getAccountPuuid("PlayerOne", "BR1")

        assertEquals("1234abcd", puuid)
    }

    @Test
    fun getSummonerLevelTest() = runBlocking {
        val mockResponse = MockResponse()
            .setBody("""{"id": "abcd1234", "accountId": "account1234", "puuid": "1234abcd", "profileIconId": "789", "revisionDate": "1616196498000", "summonerLevel": 30}""")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val summonerLevel = riotRepository.getSummonerLevel("1234abcd")

        assertEquals(30, summonerLevel)
    }

    @Test
    fun getTopChampionMasteriesTest() = runBlocking {
        val mockResponse = MockResponse()
            .setBody(
                """
                [
                    {"championId": 1, "championLevel": 7, "championPoints": 50000, "lastPlayTime": 1616196498000}
                ]
                """
            )
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val championMasteries = riotRepository.getTopChampionMasteries("1234abcd")

        assertEquals(1, championMasteries.size)
        assertEquals(1, championMasteries[0].championId)
        assertEquals(7, championMasteries[0].championLevel)
    }

    @Test
    fun getMatchIdsTest() = runBlocking {
        val mockResponse = MockResponse()
            .setBody("""["BR1_123456", "BR1_123457"]""")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val matchIds = riotRepository.getMatchIds("1234abcd")

        assertEquals(2, matchIds.size)
        assertEquals("BR1_123456", matchIds[0])
    }

    @Test
    fun getMatchDetailsTest() = runBlocking {
        val mockResponse = MockResponse()
            .setBody(
                """
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
            )
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val matchDetails = riotRepository.getMatchDetails("BR1_123456")

        assertEquals("BR1_123456", matchDetails.metadata.matchId)
        assertEquals(1800, matchDetails.info.gameDuration)
        assertEquals("CLASSIC", matchDetails.info.gameMode)
        assertEquals(1, matchDetails.info.participants.size)
        assertEquals("PlayerOne", matchDetails.info.participants[0].riotIdGameName)
    }
}
