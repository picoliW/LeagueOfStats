import android.util.Log
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.Activity
import io.appium.java_client.android.AndroidDriver
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL
import java.util.concurrent.TimeUnit

class RandomChampionsActivityTest {
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
        appDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)

        appDriver.executeScript("mobile: shell", mapOf("command" to "am start -n com.example.lol/.ui.activities.HomeActivity"))
    }

    @Test
    fun testNavigateToRandomChampionsActivity() {
        Log.d("AppiumPageSource", appDriver.pageSource)

        appDriver.findElement(AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(1)"))
            .click()

        Thread.sleep(20000)

        appDriver.findElement(AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(0)"))
            .click()

        Thread.sleep(7000)

        val champions = appDriver.findElements(AppiumBy.ByAndroidUIAutomator("new UiSelector().descriptionMatches(\".* Icon\")"))
        val randomChampion = champions.random()
        randomChampion.click()

        Thread.sleep(2000)

    }
}