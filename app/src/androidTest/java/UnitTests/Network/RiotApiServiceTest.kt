import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.lol.data.network.RiotAccountApi

@ExperimentalCoroutinesApi
class RiotApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: RiotAccountApi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(RiotAccountApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetAccountByRiotId() = runBlocking {
        val mockResponse = MockResponse()
            .setBody("""
                {
                    "puuid": "sample-puuid",
                    "gameName": "SampleName",
                    "tagLine": "1234"
                }
            """.trimIndent())
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val response = api.getAccountByRiotId("SampleName", "1234")

        assertEquals("sample-puuid", response.puuid)
        assertEquals("SampleName", response.gameName)
        assertEquals("1234", response.tagLine)
    }
}
