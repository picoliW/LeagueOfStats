import io.appium.java_client.android.AndroidDriver
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL
import java.util.concurrent.TimeUnit

class ChampionActivityTest {
    private lateinit var driver: WebDriver

    @Before
    fun setUp() {
        val capabilities = DesiredCapabilities().apply {
            setCapability("platformName", "Android")
            setCapability("deviceName", "test")
            setCapability("appPackage", "com.example.lol")
            setCapability("appActivity", ".ui.activities.ChampionActivity")
            setCapability("automationName", "UiAutomator2")
            setCapability("udid", "emulator-5554")
        }

        driver = AndroidDriver(URL("http://192.168.0.7:4723/"), capabilities)
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS)
    }

    @Test
    fun testCheckChampionDetails() {
        val championName: WebElement = driver.findElement(By.id("com.example.lol:id/champion_name"))
        val hpStat: WebElement = driver.findElement(By.id("com.example.lol:id/hp_stat"))
        val atkDamageStat: WebElement = driver.findElement(By.id("com.example.lol:id/atk_damage_stat"))

        assertTrue("Champion name should be displayed", championName.isDisplayed)
        assertTrue("Champion HP stat should be displayed", hpStat.isDisplayed)
        assertTrue("Champion attack damage stat should be displayed", atkDamageStat.isDisplayed)
    }
}
