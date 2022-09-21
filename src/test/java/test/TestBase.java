package test;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.interactions.Actions;

public class TestBase {
    public static Logger logger = LogManager.getLogger(TestBase.class.getName());
    Boolean openExternal;
    String site;
    WebDriver driver;

    public static boolean fileExists(String directoryPath, String fileName) {
        /* checks if a file exists, you pass a path to its folder and the filename */
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            return false;
        }

        File[] dirContents = dir.listFiles();

        for (int i = 0; i < dirContents.length; i++) {
            if (dirContents[i].getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            TestBase instance = new TestBase();
            instance.setUp();
            instance.all();
            instance.tearDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws InterruptedException {

        /* absolute path to chromedriver file on c:\ drive */
        String absolutePath = "c:\\Program Files\\chromedriver";
        boolean driverInstalled = fileExists(absolutePath, "chromedriver.exe");
        boolean driverIncluded = fileExists("drivers/", "chromedriver.exe");
        if (driverInstalled) {
            logger.debug("ChromeDriver is in \"" + absolutePath + "\" folder");
            System.setProperty("webdriver.chrome.driver", absolutePath + "\\chromedriver.exe");
        } else if (driverIncluded) {
            logger.debug("ChromeDriver is included in project files");
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        } else {
            logger.error("ChromeDriver file not found");
        }

        /** create chrome options and add argument to run it maximized */
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        /** initialize chromedriver with previously specified options and open browser */
        driver = new ChromeDriver(options);
    }

    public void scrollAndRepeat(WebElement element) {
        /** recursively tries to click a link, then scroll a few hundred pixels and try again if unsuccessful */
        try {
            if (element != null) {
                element.click();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JavascriptExecutor Js1 = (JavascriptExecutor) driver;
            Js1.executeScript("window.scrollBy(0,360)");
            scrollAndRepeat(element);
        }
    }

    public void crawlPageOld() throws InterruptedException {
        List<WebElement> links;
        links = driver.findElements(By.xpath("(//h4[@class='entry-title title'])/a"));
        logger.debug("Number of article links on page: " + links.size() + ".");

        /* prints all anchors and URLs */
        for (WebElement link : links) {
            String anchor = link.getText();
            String url = link.getAttribute("href");
            logger.debug(anchor + "\t" + url);
        }

        /* opens each article on page */
        for (WebElement link : links) {
            String anchor = link.getText();
            String url = link.getAttribute("href");
            if (url.startsWith(site) || openExternal) {
                scrollAndRepeat(link);
                Thread.sleep(1000);
                driver.navigate().back();
                logger.debug("Link \"" + anchor + "\" visited.");
            } else {
                logger.warn("Link \"" + anchor + "\" leads to external site.");
            }
        }
    }

    public void crawlPage() throws InterruptedException {
        List<WebElement> links;
        links = driver.findElements(By.xpath("(//h4[@class='entry-title title'])/a"));
        logger.debug("Number of article links on page: " + links.size() + ".");

        List<Href> articles = new ArrayList();
        for (WebElement link : links) {
            String text = link.getText();
            String address = link.getAttribute("href");
            articles.add(new Href(text, address));
        }

        /** prints all anchors and URLs */
        for (Href article : articles) {
            logger.debug(article.anchor + "\t" + article.url);
        }

        /** opens each article on page */
        for (Href article : articles) {
            /** opens a link only if it's on the same domain or openExternal property is set to true */
            if (article.url.startsWith(site) || openExternal) {
                driver.get(article.url);
                Thread.sleep(1000);
                /** add whatever you want to do on each visited article here */
                driver.navigate().back();
                Thread.sleep(1000);
                logger.debug("Link \"" + article.anchor + "\" visited.");
            } else {
                logger.warn("Link \"" + article.anchor + "\" leads to external site.");
            }
        }
    }

    @Test
    public void all() throws InterruptedException {
        /** reading parameters from properties file */
        String[] values = {"NULL"};
        try {
            GetPropertyValues properties = new GetPropertyValues();
            values = properties.getPropValues();
        } catch (IOException e) {
            e.printStackTrace();
        }

        site = values[0];
        openExternal = Boolean.parseBoolean(values[1]);

        /** opens the main page */
        driver.get(site);

        /** run for the first page */
        crawlPage();

        /** check if there are more pages and crawl them too */
        WebElement lastPage = null;
        int numberOfPages = 0;
        try {
            Thread.sleep(500);
            lastPage = driver.findElement(By.xpath("//a[@class='page-numbers'][last()]"));
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (NoSuchElementException e) {
            //e.printStackTrace();
            logger.debug("There is only one page on this site.");
        }

        if (lastPage != null) {
            numberOfPages = Integer.parseInt(lastPage.getText());
            logger.debug("There is more than one page on this site. Number of pages: " + numberOfPages + ".");

            /** click and crawl consecutive pages */
            WebElement page;
            for (int j = 2; j <= numberOfPages; j++) {
                String xpathExpression = "(//a[@class='page-numbers' and text()='" + j + "'])";
                page = driver.findElement(By.xpath(xpathExpression));
                page.click();
                crawlPage();
            }
            logger.debug("All pages crawled.");
        }
        Assert.assertTrue(true);
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
