import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import com.example.lol.data.database.ChampionDao
import com.example.lol.data.database.ChampionDatabase
import com.example.lol.data.database.ChampionStatsEntity
import com.example.lol.data.models.ChampionStats
import com.example.lol.data.models.Sprite
import com.example.lol.data.models.Stats
import com.example.lol.repository.fetchAllChampions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.json.JSONArray
import org.mockito.Mockito.*
import org.junit.Before
import org.junit.Test

import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

@ExperimentalCoroutinesApi
class FetchAllChampionsTest {

    private lateinit var testDatabase: MutableList<ChampionStatsEntity>
    private val testDispatcher = TestCoroutineDispatcher()

    fun ChampionStatsEntity.toChampionStats(): ChampionStats {
        return ChampionStats(
            id = this.id,
            key = this.key,
            name = this.name,
            title = this.title,
            tags = this.tags.split(",").map { it.trim() },
            stats = Stats(
                hp = this.hp,
                hpperlevel = this.hpperlevel,
                mp = this.mp,
                mpperlevel = this.mpperlevel,
                movespeed = this.movespeed,
                armor = this.armor,
                armorperlevel = this.armorperlevel,
                spellblock = this.spellblock,
                spellblockperlevel = this.spellblockperlevel,
                attackrange = this.attackrange,
                hpregen = this.hpregen,
                hpregenperlevel = this.hpregenperlevel,
                mpregen = this.mpregen,
                mpregenperlevel = this.mpregenperlevel,
                crit = this.crit,
                critperlevel = this.critperlevel,
                attackdamage = this.attackdamage,
                attackdamageperlevel = this.attackdamageperlevel,
                attackspeedperlevel = this.attackspeedperlevel,
                attackspeed = this.attackspeed
            ),
            icon = this.icon,
            sprite = Sprite(
                url = this.spriteUrl,
                x = this.spriteX,
                y = this.spriteY
            ),
            description = this.description,
            isFavorited = false
        )
    }


    @Before
    fun setup() {
        testDatabase = mutableListOf(
            ChampionStatsEntity(
                id = "1",
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
                translatedTitle = "titulo"
            )
        )
    }

    @Test
    fun testFetchAllChampionsFromCache() = runBlockingTest(testDispatcher) {
        val championsState = mutableStateOf<List<ChampionStats>>(emptyList())

        val cachedChampions = testDatabase
        championsState.value = cachedChampions.map { it.toChampionStats() }

        assert(championsState.value.isNotEmpty())
        assert(championsState.value[0].name == "Aatrox")
    }

    @Test
    fun testFetchAllChampionsFromNetwork() = runBlockingTest(testDispatcher) {
        val championsState = mutableStateOf<List<ChampionStats>>(emptyList())

        val networkResponse = "[{\"id\":\"1\", \"key\":\"Aatrox\", \"name\":\"Aatrox\", \"title\":\"The Darkin Blade\", \"tags\":[\"Fighter\"], \"stats\":{\"hp\":600, \"hpperlevel\":90}, \"sprite\":{\"url\":\"spriteUrl\", \"x\":0, \"y\":0}, \"icon\":\"iconUrl\", \"description\":\"A champion\"}]"

        val networkChampions = parseNetworkResponse(networkResponse)
        championsState.value = networkChampions

        assert(championsState.value.isNotEmpty())
        assert(championsState.value[0].name == "Aatrox")
    }

    private fun parseNetworkResponse(response: String): List<ChampionStats> {
        val champions = mutableListOf<ChampionStats>()

        val jsonArray = JSONArray(response)
        for (i in 0 until jsonArray.length()) {
            val championJson = jsonArray.getJSONObject(i)

            val statsJson = championJson.getJSONObject("stats")
            val spriteJson = championJson.getJSONObject("sprite")

            val champion = ChampionStats(
                id = championJson.getString("id"),
                key = championJson.getString("key"),
                name = championJson.getString("name"),
                title = championJson.getString("title"),
                tags = championJson.getJSONArray("tags").let { jsonArray ->
                    List(jsonArray.length()) { index -> jsonArray.getString(index) }
                },
                stats = Stats(
                    hp = statsJson.optInt("hp", 0),
                    hpperlevel = statsJson.optInt("hpperlevel", 0),
                    mp = statsJson.optInt("mp", 0),
                    mpperlevel = statsJson.optInt("mpperlevel", 0),
                    movespeed = statsJson.optInt("movespeed", 0),
                    armor = statsJson.optDouble("armor", 0.0),
                    armorperlevel = statsJson.optDouble("armorperlevel", 0.0),
                    spellblock = statsJson.optDouble("spellblock", 0.0),
                    spellblockperlevel = statsJson.optDouble("spellblockperlevel", 0.0),
                    attackrange = statsJson.optInt("attackrange", 0),
                    hpregen = statsJson.optDouble("hpregen", 0.0),
                    hpregenperlevel = statsJson.optDouble("hpregenperlevel", 0.0),
                    mpregen = statsJson.optDouble("mpregen", 0.0),
                    mpregenperlevel = statsJson.optDouble("mpregenperlevel", 0.0),
                    crit = statsJson.optDouble("crit", 0.0),
                    critperlevel = statsJson.optDouble("critperlevel", 0.0),
                    attackdamage = statsJson.optDouble("attackdamage", 0.0),
                    attackdamageperlevel = statsJson.optDouble("attackdamageperlevel", 0.0),
                    attackspeedperlevel = statsJson.optDouble("attackspeedperlevel", 0.0),
                    attackspeed = statsJson.optDouble("attackspeed", 0.0)
                ),
                icon = championJson.getString("icon"),
                sprite = Sprite(
                    url = spriteJson.getString("url"),
                    x = spriteJson.getInt("x"),
                    y = spriteJson.getInt("y")
                ),
                description = championJson.getString("description"),
                isFavorited = false
            )

            champions.add(champion)
        }
        return champions
    }

}

