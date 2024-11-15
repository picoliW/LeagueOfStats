import android.util.Log
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import java.time.Duration
import java.util.concurrent.TimeUnit

class ChampionsActivitySpeakerTest {
    private lateinit var appDriver: AndroidDriver

    private fun getAppiumDriver(): AndroidDriver {
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

            return AndroidDriver(URL("http://192.168.0.7:4723/"), this)
        }
    }

    @Before
    fun setUp() {
        appDriver = getAppiumDriver()
        appDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS)

        appDriver.executeScript("mobile: shell", mapOf("command" to "am start -n com.example.lol/.ui.activities.HomeActivity"))
    }

    @Test
    fun testSpeakerButton() {
        val wait = WebDriverWait(appDriver, Duration.ofSeconds(20))

        Thread.sleep(4000)

        val firstButton = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
            AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(0)")
        ))
        Log.d("axdsadsadasdsad", "vai clicar no primeiro")
        firstButton.click()
        Log.d("axdsadsadasdsad", "clicou no primeiro")

        Thread.sleep(7000)

        val randomChampionElement = wait.until(ExpectedConditions.elementToBeClickable(
            AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.view.View\").instance(2)")
        ))
        Log.d("axdsadsadasdsad", "vai clicar no segundo")
        randomChampionElement.click()
        Log.d("axdsadsadasdsad", "clicou no segundo")

        val speakerIcon = wait.until(ExpectedConditions.elementToBeClickable(
            AppiumBy.ByAndroidUIAutomator("new UiSelector().description(\"Speaker Icon\")")
        ))
        speakerIcon.click()

        Thread.sleep(3500)
    }
}