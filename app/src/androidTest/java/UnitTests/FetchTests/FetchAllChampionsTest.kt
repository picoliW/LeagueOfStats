package UnitTests.FetchTests

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.test.core.app.ApplicationProvider
import com.example.lol.data.models.ChampionStats
import com.example.lol.repository.fetchAllChampions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class FetchAllChampionsTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start(3001)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testFetchAllChampions() = runTest {
        val champions = mutableStateOf<List<ChampionStats>>(emptyList())

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """[
                    {
                        "id": "1",
                        "key": "Aatrox",
                        "name": "Aatrox",
                        "title": "the Darkin Blade",
                        "tags": ["Fighter"],
                        "stats": {
                            "hp": 580,
                            "hpperlevel": 90,
                            "mp": 0,
                            "mpperlevel": 0,
                            "movespeed": 345,
                            "armor": 38,
                            "armorperlevel": 3.25,
                            "spellblock": 32.1,
                            "spellblockperlevel": 1.25,
                            "attackrange": 175,
                            "hpregen": 8,
                            "hpregenperlevel": 0.75,
                            "mpregen": 0,
                            "mpregenperlevel": 0,
                            "crit": 0,
                            "critperlevel": 0,
                            "attackdamage": 60,
                            "attackdamageperlevel": 5,
                            "attackspeedperlevel": 2.5,
                            "attackspeed": 0.651
                        },
                        "sprite": {
                            "url": "https://example.com/sprite.png",
                            "x": 0,
                            "y": 0
                        },
                        "description": "Aatrox's description"
                    }
                ]"""
            )
        mockWebServer.enqueue(mockResponse)

        val testDispatcher = StandardTestDispatcher(testScheduler)
        val context = ApplicationProvider.getApplicationContext<Context>()

        withContext(testDispatcher) {
            fetchAllChampions(champions, context, size = 20, page = 1)
        }

        testScheduler.advanceUntilIdle()


        assertEquals("Aatrox", champions.value.first().name)
        assertEquals("the Darkin Blade", champions.value.first().title)
    }
}


