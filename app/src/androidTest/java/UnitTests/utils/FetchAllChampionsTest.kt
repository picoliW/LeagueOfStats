import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.lol.data.database.ChampionDao
import com.example.lol.data.database.ChampionDatabase
import com.example.lol.data.database.ChampionStatsEntity
import com.example.lol.data.models.ChampionStats
import com.example.lol.repository.fetchAllChampions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.mockito.Mockito.*
import org.junit.Before
import org.junit.Test

import java.net.HttpURLConnection
import java.net.URL

@ExperimentalCoroutinesApi
class FetchAllChampionsTest {

    private lateinit var mockContext: Context
    private lateinit var mockChampionDao: ChampionDao
    private lateinit var mockDatabase: ChampionDatabase
    private lateinit var mockHttpURLConnection: HttpURLConnection
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        mockContext = mock(Context::class.java)
        mockChampionDao = mock(ChampionDao::class.java)
        mockDatabase = mock(ChampionDatabase::class.java)
        mockHttpURLConnection = mock(HttpURLConnection::class.java)

        `when`(mockDatabase.championDao()).thenReturn(mockChampionDao)
        `when`(mockContext.resources.configuration.locales[0].language).thenReturn("pt")
    }

    @Test
    fun testFetchAllChampionsFromCache() = runBlockingTest {
        val championsState = mutableStateOf<List<ChampionStats>>(emptyList())
        val cachedChampions = listOf(
            ChampionStatsEntity(id = "1",
                key = "Aatrox",
                name = "Aatrox",
                title = "The Darkin Blade",
                tags = "Fighter",
                hp = 600,
                hpperlevel = 90,
                mp = 0,
                mpperlevel = 0,
                movespeed = 345,
                armor = 38.0,
                armorperlevel = 3.5,
                spellblock = 32.0,
                spellblockperlevel = 1.25,
                attackrange = 175,
                hpregen = 8.0,
                hpregenperlevel = 0.5,
                mpregen = 0.0,
                mpregenperlevel = 0.0,
                crit = 0.0,
                critperlevel = 0.0,
                attackdamage = 60.0,
                attackdamageperlevel = 3.0,
                attackspeedperlevel = 2.5,
                attackspeed = 0.625,
                icon = "iconUrl",
                spriteUrl = "spriteUrl",
                spriteX = 0,
                spriteY = 0,
                description = "A champion",
                translatedTitle = "titulo"),
        )

        `when`(mockChampionDao.getAllChampions()).thenReturn(cachedChampions)

        fetchAllChampions(championsState, mockContext, 20, 1)

        assert(championsState.value.isNotEmpty())
    }

    @Test
    fun testFetchAllChampionsFromNetwork() = runBlockingTest {
        val championsState = mutableStateOf<List<ChampionStats>>(emptyList())

        val url = URL("http://girardon.com.br:3001/champions?page=1&size=20")
        `when`(url.openConnection()).thenReturn(mockHttpURLConnection)
        `when`(mockHttpURLConnection.inputStream.bufferedReader().use { it.readText() }).thenReturn("[{\"id\":\"1\", \"key\":\"Aatrox\", \"name\":\"Aatrox\", \"title\":\"The Darkin Blade\", \"tags\":[\"Fighter\"], \"stats\":{\"hp\":600, \"hpperlevel\":90}, \"sprite\":{\"url\":\"spriteUrl\", \"x\":0, \"y\":0}, \"icon\":\"iconUrl\", \"description\":\"A champion\"}]")

        fetchAllChampions(championsState, mockContext, 20, 1)

        assert(championsState.value.isNotEmpty())
    }
}
