import android.content.Context
import com.example.lol.data.models.ItemsModel
import com.example.lol.data.models.Price
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.json.JSONArray
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ItemModalTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var context: Context

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    private suspend fun fetchRandomItems(
        page: Int,
        size: Int,
        onResult: (List<ItemsModel>) -> Unit
    ) {
        val allItems = mutableListOf<ItemsModel>()
        var currentPage = page
        var hasMore = true

        while (hasMore) {
            val response = fetchDataFromSource(currentPage, size)
            val jsonArray = JSONArray(response)

            if (jsonArray.length() == 0) {
                hasMore = false
            } else {
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val priceJson = item.getJSONObject("price")
                    val totalPrice = priceJson.getInt("total")

                    if (totalPrice > 2000 && !item.getString("name").contains("enchantment:", ignoreCase = true)) {
                        val itemModel = ItemsModel(
                            name = item.getString("name"),
                            description = item.getString("description"),
                            price = Price(
                                base = priceJson.getInt("base"),
                                total = totalPrice,
                                sell = priceJson.getInt("sell")
                            ),
                            purchasable = item.getBoolean("purchasable"),
                            iconUrl = item.getString("icon").replace("http://", "https://")
                        )
                        allItems.add(itemModel)
                    }
                }
                currentPage++
            }
        }

        onResult(allItems.shuffled().take(5))
    }

    private fun fetchDataFromSource(page: Int, size: Int): String {
        return if (page == 1) {
            """
                [
                    {"name": "Item1", "description": "Description1", "price": {"base": 1000, "total": 2500, "sell": 1500}, "purchasable": true, "icon": "http://iconUrl1"},
                    {"name": "Item2", "description": "Description2", "price": {"base": 1500, "total": 3000, "sell": 2000}, "purchasable": true, "icon": "http://iconUrl2"}
                ]
            """
        } else {
            "[]"
        }
    }

    @Test
    fun testFetchRandomItems() = runBlockingTest {
        val resultItems = mutableListOf<ItemsModel>()

        fetchRandomItems(1, 20) { items ->
            resultItems.addAll(items)
        }

        assertTrue(resultItems.isNotEmpty())
        assertEquals(2, resultItems.size)
        assertTrue(resultItems.any { it.name == "Item1" })
        assertTrue(resultItems.any { it.name == "Item2" })
    }
}
