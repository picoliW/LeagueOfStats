package com.example.lol.ui.activities

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.test.core.app.ApplicationProvider
import com.example.lol.database.ChampionDatabase
import com.example.lol.database.ChampionStatsEntity
import com.example.lol.models.ChampionStats
import com.example.lol.models.Sprite
import com.example.lol.models.Stats
import kotlinx.coroutines.runBlocking

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class ChampionsScreenTest {

    private lateinit var champions: List<ChampionStats>

    @Before
    fun setUp() {
        champions = listOf(
            ChampionStats(
                id = "aatrox",
                key = "266",
                name = "Aatrox",
                title = "the Darkin Blade",
                tags = listOf("Fighter", "Tank"),
                stats = Stats(
                    hp = 580,
                    hpperlevel = 90,
                    mp = 0,
                    mpperlevel = 0,
                    movespeed = 345,
                    armor = 38.0,
                    armorperlevel = 3.25,
                    spellblock = 32.1,
                    spellblockperlevel = 1.25,
                    attackrange = 175,
                    hpregen = 3.0,
                    hpregenperlevel = 1.0,
                    mpregen = 0.0,
                    mpregenperlevel = 0.0,
                    crit = 0.0,
                    critperlevel = 0.0,
                    attackdamage = 60.0,
                    attackdamageperlevel = 5.0,
                    attackspeedperlevel = 2.5,
                    attackspeed = 0.651
                ),
                icon = "https://ddragon.leagueoflegends.com/cdn/10.23.1/img/champion/Aatrox.png",
                sprite = Sprite(
                    url = "https://ddragon.leagueoflegends.com/cdn/10.23.1/img/sprite/champion0.png",
                    x = 0,
                    y = 0
                ),
                description = "Once honored defenders of Shurima against the Void, Aatrox and his brethren would eventually become an even greater threat to Runeterra, and were defeated only by cunning mortal sorcery."
            ),
            ChampionStats(
                id = "ahri",
                key = "103",
                name = "Ahri",
                title = "the Nine-Tailed Fox",
                tags = listOf("Mage", "Assassin"),
                stats = Stats(
                    hp = 526,
                    hpperlevel = 92,
                    mp = 418,
                    mpperlevel = 25,
                    movespeed = 330,
                    armor = 20.88,
                    armorperlevel = 3.5,
                    spellblock = 30.0,
                    spellblockperlevel = 0.5,
                    attackrange = 550,
                    hpregen = 5.5,
                    hpregenperlevel = 0.6,
                    mpregen = 8.0,
                    mpregenperlevel = 0.8,
                    crit = 0.0,
                    critperlevel = 0.0,
                    attackdamage = 53.04,
                    attackdamageperlevel = 3.0,
                    attackspeedperlevel = 2.0,
                    attackspeed = 0.668
                ),
                icon = "https://ddragon.leagueoflegends.com/cdn/10.23.1/img/champion/Ahri.png",
                sprite = Sprite(
                    url = "https://ddragon.leagueoflegends.com/cdn/10.23.1/img/sprite/champion0.png",
                    x = 48,
                    y = 0
                ),
                description = "Innately connected to the latent power of Runeterra, Ahri is a vastaya who can reshape magic into orbs of raw energy."
            )
        )
    }

    @Test
    fun testChampionFilter_byName() {
        val searchQuery = "Aatrox"
        val filteredChampions = champions.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

        assertEquals(1, filteredChampions.size)
        assertEquals("Aatrox", filteredChampions[0].name)
    }

    @Test
    fun testChampionFilter_byTitle() {
        val searchQuery = "the Nine-Tailed Fox"
        val filteredChampions = champions.filter {
            it.title.contains(searchQuery, ignoreCase = true)
        }

        assertEquals(1, filteredChampions.size)
        assertEquals("Ahri", filteredChampions[0].name)
    }

    @Test
    fun testChampionFilter_byTag() {
        val searchQuery = "Fighter"
        val filteredChampions = champions.filter {
            it.tags.contains(searchQuery)
        }

        assertEquals(1, filteredChampions.size)
        assertEquals("Aatrox", filteredChampions[0].name)
    }

    @Test
    fun testChampionFilter_byDescription() {
        val searchQuery = "vastaya"
        val filteredChampions = champions.filter {
            it.description.contains(searchQuery, ignoreCase = true)
        }

        assertEquals(1, filteredChampions.size)
        assertEquals("Ahri", filteredChampions[0].name)
    }

    @Test
    fun testChampionFilter_byKey() {
        val searchQuery = "266"
        val filteredChampions = champions.filter {
            it.key == searchQuery
        }

        assertEquals(1, filteredChampions.size)
        assertEquals("Aatrox", filteredChampions[0].name)
    }

    @Test
    fun testChampionFilter_noResults() {
        val searchQuery = "NonExistentChampion"
        val filteredChampions = champions.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

        assertTrue(filteredChampions.isEmpty())
    }

    @Test
    fun testTranslationFunction() {
        val originalText = "the Nine-Tailed Fox"
        val translatedText = translateText(originalText, "pt")

        assertNotNull(translatedText)
        assertEquals("a raposa de nove caudas", translatedText)
    }

    @Test
    fun testSaveChampionToDatabase() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = ChampionDatabase.getDatabase(context)

        val championDao = db.championDao()

        val champion = ChampionStatsEntity(
            id = "yasuo",
            key = "157",
            name = "Yasuo",
            title = "the Unforgiven",
            translatedTitle = "O Imperdoável",
            tags = "Fighter,Mage",
            hp = 490,
            hpperlevel = 87,
            mp = 100,
            mpperlevel = 40,
            movespeed = 345,
            armor = 30.0,
            armorperlevel = 3.4,
            spellblock = 32.0,
            spellblockperlevel = 1.25,
            attackrange = 175,
            hpregen = 6.0,
            hpregenperlevel = 0.9,
            mpregen = 8.0,
            mpregenperlevel = 0.8,
            crit = 0.0,
            critperlevel = 0.0,
            attackdamage = 60.0,
            attackdamageperlevel = 3.0,
            attackspeedperlevel = 2.5,
            attackspeed = 0.658,
            icon = "https://ddragon.leagueoflegends.com/cdn/10.23.1/img/champion/Yasuo.png",
            spriteUrl = "https://ddragon.leagueoflegends.com/cdn/10.23.1/img/sprite/champion1.png",
            spriteX = 96,
            spriteY = 0,
            description = "An Ionian of deep resolve, Yasuo is a nimble swordsman who wields the air itself against his enemies."
        )

        championDao.insertAll(listOf(champion))

        val retrievedChampion = championDao.getChampionById("yasuo")

        assertNotNull(retrievedChampion)
        assertEquals("Yasuo", retrievedChampion?.name)
        assertEquals("the Unforgiven", retrievedChampion?.title)
    }

    @Test
    fun testLoadChampionsFromDatabase() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = ChampionDatabase.getDatabase(context)
        val championDao = db.championDao()

        val championsToInsert = listOf(
            ChampionStatsEntity(
                id = "yasuo",
                key = "157",
                name = "Yasuo",
                title = "the Unforgiven",
                translatedTitle = "O Imperdoável",
                tags = "Fighter,Mage",
                hp = 490,
                hpperlevel = 87,
                mp = 100,
                mpperlevel = 40,
                movespeed = 345,
                armor = 30.0,
                armorperlevel = 3.4,
                spellblock = 32.0,
                spellblockperlevel = 1.25,
                attackrange = 175,
                hpregen = 6.0,
                hpregenperlevel = 0.9,
                mpregen = 8.0,
                mpregenperlevel = 0.8,
                crit = 0.0,
                critperlevel = 0.0,
                attackdamage = 60.0,
                attackdamageperlevel = 3.0,
                attackspeedperlevel = 2.5,
                attackspeed = 0.658,
                icon = "https://ddragon.leagueoflegends.com/cdn/10.23.1/img/champion/Yasuo.png",
                spriteUrl = "https://ddragon.leagueoflegends.com/cdn/10.23.1/img/sprite/champion1.png",
                spriteX = 96,
                spriteY = 0,
                description = "An Ionian of deep resolve, Yasuo is a nimble swordsman who wields the air itself against his enemies."
            )
        )

        championDao.insertAll(championsToInsert)

        val championsFromDb = championDao.getAllChampions()

        assertEquals(1, championsFromDb.size)
        assertEquals("Yasuo", championsFromDb[0].name)
    }





}
