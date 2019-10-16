import com.saucelabs.saucerest.SauceREST;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SimpleSampleTest {

    private RemoteWebDriver driver;
    private String sauceEndpoint = System.getenv("SAUCE_ENDPOINT");
    private String username = System.getenv("SAUCE_USERNAME");
    private String accesskey = System.getenv("SAUCE_ACCESS_KEY");
    private String extendedDebugging = System.getenv("EXT_DEBUGGING");
    private String saucePerformance = System.getenv("PERFORMANCE");
    private String browserName = System.getenv("BROWSER");
    private String platformName = System.getenv("PLATFORM");
    private String browserVersion = System.getenv("BROWSER_VERSION");
    private SauceREST sauceRESTClient = new SauceREST(username, accesskey);

    @BeforeMethod
    public void setup() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("browserName", browserName);
        desiredCapabilities.setCapability("extendedDebugging", extendedDebugging);
        desiredCapabilities.setCapability("capturePerformance", saucePerformance);
        desiredCapabilities.setCapability("platform", platformName);
        desiredCapabilities.setCapability("version", browserVersion);
        desiredCapabilities.setCapability("name", "loginTest");
        this.driver = new RemoteWebDriver(new URL("https://" + username + ":" +  accesskey + "@" + sauceEndpoint + "/wd/hub"), desiredCapabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void loginTest() {
        driver.get("https://www.saucedemo.com");
        driver.findElement(By.cssSelector("input[type='text']")).sendKeys("standard_user");
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("secret_sauce");
        driver.findElement(By.cssSelector("input[type='submit']")).click();
        Assert.assertTrue(driver.findElement(By.cssSelector(".product_label")).isDisplayed());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver != null) {
            String sessionId = driver.getSessionId().toString();
            boolean status = result.isSuccess();

            // Sauce REST API (updateJob)
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("passed", status);
            sauceRESTClient.updateJobInfo(sessionId, updates);

            driver.quit();
        }

    }


}
