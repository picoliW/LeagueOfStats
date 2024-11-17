import com.example.lol.BuildConfig
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import java.time.Duration
import java.util.concurrent.TimeUnit

class SummonerActivityTest {
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
    fun testNavigateToRandomChampionsActivity() {
        val wait = WebDriverWait(appDriver, Duration.ofSeconds(20))

        val firstButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                appDriver.findElement(AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(3)"))
            ))
        firstButton.click()

        val summonerName = wait.until(ExpectedConditions.elementToBeClickable(
            appDriver.findElement(AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)"))
        ))
        summonerName.sendKeys("baba tronco")


        val summonerTag = wait.until(ExpectedConditions.elementToBeClickable(
            appDriver.findElement(AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(1)"))
        ))
        summonerTag.sendKeys("baba")

        val searchSummoner = wait.until(
            ExpectedConditions.elementToBeClickable(
                appDriver.findElement(AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.Button\")"))
            ))
        searchSummoner.click()

        val scrollToMatch = "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().className(\"android.view.View\").instance(8))"
        appDriver.findElement(AppiumBy.ByAndroidUIAutomator(scrollToMatch))

        val summonerMatch = wait.until(
            ExpectedConditions.elementToBeClickable(
                appDriver.findElement(AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.view.View\").instance(8)"))
            ))
        summonerMatch.click()

        Thread.sleep(8000)

    }
}