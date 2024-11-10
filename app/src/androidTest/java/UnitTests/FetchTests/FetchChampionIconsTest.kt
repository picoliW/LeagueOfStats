import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.test.core.app.ApplicationProvider
import com.example.lol.data.models.ChampionIconModel
import com.example.lol.repository.fetchChampionIcons
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
class FetchChampionIconsIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var context: Context

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start(3001)
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testFetchChampionIcons() = runBlockingTest {
        val mockResponse = MockResponse().setBody("")
        mockWebServer.enqueue(mockResponse)

        val icons = mutableStateOf<List<ChampionIconModel>>(emptyList())
        val latch = CountDownLatch(1)

        fetchChampionIcons(icons, context) {
            latch.countDown()
        }

        latch.await(5, TimeUnit.SECONDS)

        assertTrue(icons.value.isNotEmpty())
        assertEquals(152, icons.value.size)
        assertEquals("Aatrox", icons.value[0].name)
        assertEquals(266, icons.value[0].key)
        assertEquals("https://ddragon.leagueoflegends.com/cdn/10.23.1/img/champion/Aatrox.png", icons.value[0].iconUrl)
    }
}
