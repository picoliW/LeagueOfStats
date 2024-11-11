import io.appium.java_client.android.AndroidDriver
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import java.time.Duration

class RandomChampionsActivityTest {
    private lateinit var driver: WebDriver

    @Before
    fun setUp() {
        val capabilities = DesiredCapabilities().apply {
            setCapability("platformName", "Android")
            setCapability("deviceName", "samsung SM-G611MT")
            setCapability("appPackage", "com.example.lol")
            setCapability("appActivity", ".ui.activities.HomeActivity")
            setCapability("automationName", "UiAutomator2")
            setCapability("udid", "330098f0268f358b")
            setCapability("noReset", true)
        }

        driver = AndroidDriver(URL("http://192.168.0.7:4723/"), capabilities)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
    }

    @Test
    fun testNavigateToRandomChampionsActivity() {
        val wait = WebDriverWait(driver, Duration.ofSeconds(20))
        val randomChampionsButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//android.view.View[@content-desc='randomChampionsButton']"))
        )
        randomChampionsButton.click()

        val randomChampionTitle = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@text, 'Random Champions')]"))
        )
        assertTrue("Failed to navigate to RandomChampionsActivity", randomChampionTitle.isDisplayed)
    }

    @After
    fun tearDown() {
        driver.quit()
    }
}
