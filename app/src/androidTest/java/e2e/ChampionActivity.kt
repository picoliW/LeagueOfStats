//import io.appium.java_client.android.AndroidDriver
//import org.junit.After
//import org.junit.Assert.assertTrue
//import org.junit.Before
//import org.junit.Test
//import org.openqa.selenium.By
//import org.openqa.selenium.WebDriver
//import org.openqa.selenium.WebElement
//import org.openqa.selenium.remote.DesiredCapabilities
//import java.net.URL
//import java.util.concurrent.TimeUnit
//
//class ChampionActivityTest {
//    private lateinit var driver: WebDriver
//
//    @Before
//    fun setUp() {
//        val capabilities = DesiredCapabilities().apply {
//            setCapability("platformName", "Android")
//            setCapability("deviceName", "test")
//            setCapability("appPackage", "")
//            setCapability("appActivity", ".ui.activities.ChampionActivity")
//            setCapability("automationName", "UiAutomator2")
//            setCapability("udid", "emulator-5556")
//        }
//
//        driver = AndroidDriver(URL("http://192.168.6.96:4723/"), capabilities)
//        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS)
//    }
//
//    @Test
//    fun testCheckChampionDetails() {
//        val test = true
//
//        assertTrue(test)
//    }
//}
