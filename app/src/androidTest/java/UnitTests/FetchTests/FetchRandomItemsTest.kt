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
class FetchRandomItemsIntegrationTest {

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
        val mockResponse = MockResponse().setBody(
            """
        [
            {"name": "Item1", "description": "Description1", "price": {"base": 1000, "total": 2500, "sell": 1500}, "purchasable": true, "icon": "http://iconUrl1"},
            {"name": "Item2", "description": "Description2", "price": {"base": 1500, "total": 3000, "sell": 2000}, "purchasable": true, "icon": "http://iconUrl2"},
            {"name": "Item3", "description": "Description3", "price": {"base": 500, "total": 1800, "sell": 1000}, "purchasable": true, "icon": "http://iconUrl3"},
            {"name": "Item4", "description": "Description4", "price": {"base": 1200, "total": 2700, "sell": 1700}, "purchasable": true, "icon": "http://iconUrl4"},
            {"name": "Item5", "description": "Description5", "price": {"base": 800, "total": 2100, "sell": 1300}, "purchasable": true, "icon": "http://iconUrl5"}
        ]
    """
        )
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
