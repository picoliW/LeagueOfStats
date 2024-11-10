import androidx.test.core.app.ApplicationProvider
import com.example.lol.data.models.ItemsModel
import com.example.lol.repository.fetchRandomItems
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
class FetchRandomItemsTest {

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
    fun testFetchRandomItems() = runBlockingTest {
        val mockResponse = MockResponse().setBody("")
        mockWebServer.enqueue(mockResponse)

        val resultItems = mutableListOf<ItemsModel>()
        val latch = CountDownLatch(1)

        fetchRandomItems(ApplicationProvider.getApplicationContext()) { items ->
            resultItems.addAll(items)
            latch.countDown()
        }

        latch.await(5, TimeUnit.SECONDS)

        assertTrue(resultItems.isNotEmpty())
        assertEquals(5, resultItems.size)
    }
}
