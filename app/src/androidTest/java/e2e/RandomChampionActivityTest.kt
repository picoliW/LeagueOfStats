import com.example.lol.BuildConfig
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import java.time.Duration
import java.util.concurrent.TimeUnit

class RandomChampionsActivityTest {
    private lateinit var appDriver: AndroidDriver

    private fun getAppiumDriver(): AndroidDriver {

        val serverIp = BuildConfig.APPIUM_SERVER_IP
        val serverUrl = "http://$serverIp:4723/"

        with(DesiredCapabilities()) {
            setCapability("platformName", "Android")
            setCapability("deviceName", "samsung SM-G611MT")
            setCapability("appPackage", "com.example.lol")
            setCapability("appActivity", ".ui.activities.HomeActivity")
            setCapability("automationName", "UiAutomator2")
            setCapability("udid", "330098f0268f358b")
            setCapability("noReset", true)
            setCapability("appium:newCommandTimeout", 100)
            setCapability("appium:enableAdbShell", true)

            return AndroidDriver(URL(serverUrl), this)
        }
    }

    @Before
    fun setUp() {
        appDriver = getAppiumDriver()
        appDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)

        appDriver.executeScript("mobile: shell", mapOf("command" to "am start -n com.example.lol/.ui.activities.HomeActivity"))
    }


    @Test
    fun testRandomChampionsAreDifferent() {
        val wait = WebDriverWait(appDriver, Duration.ofSeconds(20))

        val firstButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                appDriver.findElement(
                    AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(1)")
                )
            )
        )
        firstButton.click()

        val initialChampions = wait.until(
            ExpectedConditions.visibilityOfAllElementsLocatedBy(
                AppiumBy.ByAndroidUIAutomator("new UiSelector().descriptionMatches(\".* Icon\")")
            )
        ).map { it.getAttribute("contentDescription") }

        val sortAgain = wait.until(
            ExpectedConditions.elementToBeClickable(
                appDriver.findElement(
                    AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(0)")
                )
            )
        )
        sortAgain.click()

        val newChampions = wait.until(
            ExpectedConditions.visibilityOfAllElementsLocatedBy(
                AppiumBy.ByAndroidUIAutomator("new UiSelector().descriptionMatches(\".* Icon\")")
            )
        ).map { it.getAttribute("contentDescription") }

        assertTrue(initialChampions != newChampions)

        val champions = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
            AppiumBy.ByAndroidUIAutomator("new UiSelector().descriptionMatches(\".* Icon\")")
        ))

        val randomChampion = champions.random()
        randomChampion.click()

        Thread.sleep(2000)
    }
}