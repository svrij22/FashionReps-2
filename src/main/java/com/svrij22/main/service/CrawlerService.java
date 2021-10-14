package com.svrij22.main.service;

import com.google.gson.Gson;
import com.svrij22.main.domain.FashionItem;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

@Service
public class CrawlerService {

    public FashionItemService fashionItemService;
    public static boolean isBusy;

    public CrawlerService(FashionItemService fashionItemService) {
        this.fashionItemService = fashionItemService;
    }

    public String crawl(String sellerid){

        //
        isBusy = true;

        /*Browser Mob Proxy*/
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);

        Proxy selProxy = ClientUtil.createSeleniumProxy(proxy);

        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(CapabilityType.PROXY, selProxy);

        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxy.newHar("Request");

        /*Options*/
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
        options.setProxy(selProxy);
        options.setAcceptInsecureCerts(true);
        options.setCapability( "goog:loggingPrefs", logPrefs );

        /*Disable images*/
        //HashMap<String, Object> images = new HashMap<>();
        //images.put("images", 2);
        //HashMap<String, Object> prefs = new HashMap<>();
        //prefs.put("profile.default_content_setting_values", images);
        //options.setExperimentalOption("prefs", prefs);

        /*Webdriver*/
        WebDriverManager.chromedriver().setup();
        System.setProperty("webdriver.chrome.whitelistedIps", "");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(600, 600));
        driver.get("https://weidian.com/?userid=" + sellerid);

        /*Debug*/
        int attempts = 0;
        int prevAmount = 0;
        while (attempts < 200){
            try{
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0,10000)", "");

                //If amount of items got is under 100, click the buttons
                if (prevAmount < 500){
                    Setup(driver); //Clicks buttons
                    //ClickMoreButtons(driver); //Clicks buttons
                }

                //Sleep
                Thread.sleep(10);

                //Get logs
                HarLog harlog = proxy.getHar().getLog();
                System.out.println("Entries " + harlog.getEntries().size());

                //Get amount of items
                int amountOfItems = CountAmountOfItems(harlog);
                System.out.println("Amount of items: " + amountOfItems);

                //Set amount of items
                if (amountOfItems > prevAmount){
                    prevAmount = amountOfItems;
                    attempts = 0;
                }
                attempts += 1;
            }catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        proxy.stop();
        driver.close();

        /*Parse*/
        List<HarEntry> allEntries = proxy.getHar().getLog().getEntries();
        allEntries.forEach(harEntry -> {
            if (harEntry.getRequest().getUrl().contains("getItemList")){
                System.out.printf("Parsing ... " + allEntries.indexOf(harEntry) + " of " + allEntries.size());
                parseItems(harEntry.getResponse().getContent().getText(), sellerid);
            }
        });

        System.out.println("Done");
        isBusy = false;

        return "test";
    }

    public void parseItems(String jsonText, String sellerid){
        try{
            Gson gson = new Gson();
            JsonText jsonObject = gson.fromJson(jsonText, JsonText.class);
            System.out.println("%nParsed");
            jsonObject.result.itemList.forEach(fashionItem -> fashionItem.seller = sellerid);
            fashionItemService.saveAll(jsonObject.result.itemList);
            System.out.println("Flushed");
            System.out.printf(". Saved all Fashion Items (%s)%n", jsonObject.result.itemList.size());
        }catch (Exception e) {
            System.out.println("Could not parse json file");
        }
    }

    public boolean isBusy() {
        return isBusy;
    }

    public class JsonText{
        public Result result;
    }

    public class Result{
        public List<FashionItem> itemList;
    }

    public void Setup(WebDriver driver){

        try{
            //Click exit
            driver.findElement(By.className("e-active-newuser-coupon-close")).click();
        }catch (Exception e){  System.out.println("Exit button - Exception"); }

        //Click All Tab
        List<WebElement> webElements = driver.findElements(By.className("j-tab-item"));
        webElements.forEach(w -> {
            if (w.getText().contains("全部")) {
                try {
                    scrollToElement(w, driver);
                    w.click();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    public void ClickMoreButtons(WebDriver driver){
        /*List<WebElement> elements = driver.findElements(By.xpath("//img[@data-event='loadMore']"));
        if (elements.size() < 1) return;
        WebElement webElement = getRandomElement(elements);
        try {
            scrollToElement(webElement, driver);
            webElement.click();
            Thread.sleep(80);
        } catch (Exception e) {
            System.out.println("More button - Exception");
        }*/
    }
    public WebElement getRandomElement(List<WebElement> list)
    {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    private void scrollToElement(WebElement webElement, WebDriver webDriver) throws Exception {
        ((JavascriptExecutor)webDriver).executeScript("arguments[0].scrollIntoViewIfNeeded()", webElement);
        Thread.sleep(500);
    }

    public int CountAmountOfItems(HarLog harlog){
        int amountOfItems = 0;
        for (HarEntry log : harlog.getEntries()) {
            try{
                amountOfItems += log.getResponse().getContent().getText().split("itemId").length - 1;
            }catch (Exception ignored) {}
        }
        return amountOfItems;
    }
}