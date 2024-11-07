package UnitTests.Models

import com.example.lol.data.models.ChampionStats
import com.example.lol.data.models.Sprite
import com.example.lol.data.models.Stats
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChampionModelTest {
    private lateinit var mockChampion: ChampionStats

    @Before
    fun setUp() {
        mockChampion = ChampionStats(
            id = "123",
            key = "mockChampion",
            name = "Mock Champion",
            title = "The Test Entity",
            tags = listOf("Mage", "Fighter"),
            stats = Stats(
                hp = 1000,
                hpperlevel = 50,
                mp = 300,
                mpperlevel = 30,
                movespeed = 350,
                armor = 40.0,
                armorperlevel = 3.5,
                spellblock = 32.0,
                spellblockperlevel = 1.25,
                attackrange = 550,
                hpregen = 5.5,
                hpregenperlevel = 0.5,
                mpregen = 8.0,
                mpregenperlevel = 0.65,
                crit = 0.0,
                critperlevel = 0.0,
                attackdamage = 60.0,
                attackdamageperlevel = 4.0,
                attackspeedperlevel = 2.5,
                attackspeed = 0.675
            ),
            icon = "mock_icon_url",
            sprite = Sprite(url = "mock_sprite_url", x = 0, y = 0),
            description = "This is a test champion used for unit testing.",
            isFavorited = false
        )
    }

    @Test
    fun getHp() {
        assertEquals(1000, mockChampion.stats.hp)
    }

    @Test
    fun testGetHpPerLevel() {
        assertEquals(50, mockChampion.stats.hpperlevel)
    }

    @Test
    fun testGetMp() {
        assertEquals(300, mockChampion.stats.mp)
    }

    @Test
    fun testGetMpPerLevel() {
        assertEquals(30, mockChampion.stats.mpperlevel)
    }

    @Test
    fun testGetMoveSpeed() {
        assertEquals(350, mockChampion.stats.movespeed)
    }

    @Test
    fun testGetArmor() {
        assertEquals(40.0, mockChampion.stats.armor, 0.0)
    }
}
