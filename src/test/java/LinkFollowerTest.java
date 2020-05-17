import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@Execution(ExecutionMode.CONCURRENT)
public class LinkFollowerTest {
    private WebDriver webDriver;

    static Stream<Arguments> browsersAndUrlsProvider() {
        return Stream.of(
                arguments(BrowserType.CHROME, "https://www.saucelabs.com/"),
                arguments(BrowserType.FIREFOX, "https://www.saucelabs.com/"),
                arguments(BrowserType.FIREFOX, "https://www.selenium.dev/"),
                arguments(BrowserType.CHROME, "https://www.selenium.dev/"),
                arguments(BrowserType.CHROME, "https://www.appium.io/"),
                arguments(BrowserType.FIREFOX, "https://www.appium.io/"),
                arguments(BrowserType.CHROME, "https://github.com/"),
                arguments(BrowserType.FIREFOX, "https://github.com/")
                );
    }

    @ParameterizedTest
    @MethodSource("browsersAndUrlsProvider")
    public void justFollowingLinksTest(String browserName, String url) throws InterruptedException, MalformedURLException {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, browserName);
        String gridUrl = "http://hub:4444/wd/hub";
        webDriver = new RemoteWebDriver(new URL(gridUrl), capabilities);

        int pagesToNavigate = 5;
        int navigatedPages = 0;
        List<String> visitedUrls = new ArrayList<>();
        webDriver.get(url);
        visitedUrls.add(url);
        while (navigatedPages < pagesToNavigate) {
            navigatedPages++;
            // Just to have a couple of seconds to see the page
            Thread.sleep(2000);
            List<WebElement> elements = webDriver.findElements(By.cssSelector("a[href]"));
            for (WebElement element : elements) {
                String href = element.getAttribute("href");
                if (!href.contains("#") && !href.equals(webDriver.getCurrentUrl()) &&
                        href.startsWith("http") && !visitedUrls.contains(href)) {
                    visitedUrls.add(href);
                    System.out.println(String.format("%s - Browser: %s, Visiting url: %s",
                            Thread.currentThread().getId(), browserName, href));
                    webDriver.get(href);
                    break;
                }
            }
        }
    }

    @AfterEach
    public void quitDriver() {
        webDriver.quit();
    }

}
