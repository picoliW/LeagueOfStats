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

class RiotRepositoryTest2 {

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
    fun getMatchDetailsWithSummonerNamesTest() = runBlocking {
        mockWebServer.enqueue(
            MockResponse().setBody(
                """
                {
                    "metadata": {
                        "matchId": "BR1_123456",
                        "participants": ["puuid1", "puuid2"]
                    },
                    "info": {
                        "gameDuration": 1800,
                        "gameMode": "CLASSIC",
                        "participants": []
                    }
                }
                """
            ).setResponseCode(200)
        )
        mockWebServer.enqueue(
            MockResponse().setBody(
                """{"puuid": "puuid1", "gameName": "PlayerOne", "tagLine": "BR1"}"""
            ).setResponseCode(200)
        )
        mockWebServer.enqueue(
            MockResponse().setBody(
                """{"puuid": "puuid2", "gameName": "PlayerTwo", "tagLine": "BR2"}"""
            ).setResponseCode(200)
        )

        val (matchDetails, summonerNames) = riotRepository.getMatchDetailsWithSummonerNames("BR1_123456")

        assertEquals("BR1_123456", matchDetails.metadata.matchId)
        assertEquals(2, matchDetails.metadata.participants.size)
        assertEquals(listOf("PlayerOne#BR1", "PlayerTwo#BR2"), summonerNames)
    }

    @Test
    fun getSummonerNamesByPUUIDsTest() = runBlocking {
        mockWebServer.enqueue(
            MockResponse().setBody(
                """{"puuid": "puuid1", "gameName": "PlayerOne", "tagLine": "BR1"}"""
            ).setResponseCode(200)
        )
        mockWebServer.enqueue(
            MockResponse().setBody(
                """{"puuid": "puuid2", "gameName": "PlayerTwo", "tagLine": "BR2"}"""
            ).setResponseCode(200)
        )

        val summonerNames = riotRepository.getSummonerNamesByPUUIDs(listOf("puuid1", "puuid2"))

        assertEquals(listOf("PlayerOne#BR1", "PlayerTwo#BR2"), summonerNames)
    }

    @Test
    fun getMatchDetailsWithSummonerNamesAndChampionsTest() = runBlocking {
        mockWebServer.enqueue(
            MockResponse().setBody(
                """
                {
                    "metadata": {
                        "matchId": "BR1_123456",
                        "participants": ["puuid1"]
                    },
                    "info": {
                        "gameDuration": 1800,
                        "gameMode": "CLASSIC",
                        "participants": [
                            {
                                "puuid": "puuid1",
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
                        ]
                    }
                }
                """
            ).setResponseCode(200)
        )

        // Execute function
        val participantsData = riotRepository.getMatchDetailsWithSummonerNamesAndChampions("BR1_123456")

        // Verify results
        assertEquals(1, participantsData.size)
        assertEquals("PlayerOne", participantsData[0].riotIdGameName)
        assertEquals(101, participantsData[0].championId)
        assertEquals("ChampionOne", participantsData[0].championName)
        assertEquals("MIDDLE", participantsData[0].individualPosition)
        assertEquals("100", participantsData[0].teamId)
        assertEquals("10", participantsData[0].kills)
        assertEquals("2", participantsData[0].deaths)
        assertEquals("8", participantsData[0].assists)
        assertEquals("18", participantsData[0].champLevel)
    }
}
