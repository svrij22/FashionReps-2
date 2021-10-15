package main.java.com.svrij22.main.service;

import com.google.gson.Gson;
import main.java.com.svrij22.main.domain.FashionItem;
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
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FetchInjector {

    public FashionItemService fashionItemService;
    public static boolean isBusy;

    public FetchInjector(FashionItemService fashionItemService) {
        this.fashionItemService = fashionItemService;
    }

    public String fetch(String sellerid){

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
        options.setProxy(selProxy);
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        System.setProperty("webdriver.chrome.args", "--disable-logging");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        options.setAcceptInsecureCerts(true);
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("disable-infobars"); // disabling infobars
        options.addArguments("window-size=600,600"); // Bypass OS security

        /*Webdriver*/
        WebDriverManager.chromedriver().setup();
        System.setProperty("webdriver.chrome.whitelistedIps", "");
        WebDriver driver = new ChromeDriver(options);
        driver.get("https://weidian.com/?userid=" + sellerid);

        /*Debug*/
        int prevAmount = 0;
        int attempts = 0;
        int offset = 0;

        while (attempts < 25){
            try{
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(FetchInjection(sellerid, offset, 100), "");
                offset += 100;

                //Sleep
                Thread.sleep(200);

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

        //Stop
        proxy.stop();
        driver.close();

        parseItems(proxy.getHar().getLog(), sellerid);

        System.out.println("Done");
        isBusy = false;

        return "test";
    }

    public String FetchInjection(String sellerid, int offset, int limit){
        return "fetch(\"https://thor.weidian.com/decorate/shopDetail.tab.getItemList/1.0?param=%7B%22shopId%22:%22" + sellerid + "%22,%22tabId%22:0,%22sortOrder%22:%22desc%22,%22offset%22:"+offset+",%22limit%22:"+limit+"%7D\", {\n" +
                "  \"referrer\": \"https://weidian.com/\",\n" +
                "  \"referrerPolicy\": \"strict-origin-when-cross-origin\",\n" +
                "  \"body\": null,\n" +
                "  \"method\": \"GET\",\n" +
                "  \"mode\": \"cors\",\n" +
                "  \"credentials\": \"include\"\n" +
                "});";
    }

    public void parseItems(HarLog harLog, String sellerid){

        List<HarEntry> allEntries = harLog.getEntries();

        System.out.println("Parsing all items");
        allEntries.forEach(harEntry -> {
            if (harEntry.getRequest().getUrl().contains("getItemList")){

                System.out.printf("Parsing ... " + allEntries.indexOf(harEntry) + " of " + allEntries.size());

                try{
                    String jsonText = harEntry.getResponse().getContent().getText();

                    Gson gson = new Gson();
                    JsonText jsonObject = gson.fromJson(jsonText, JsonText.class);

                    System.out.println("Parsed");

                    jsonObject.result.itemList.forEach(fashionItem -> fashionItem.seller = sellerid);

                    System.out.println("Flushing");
                    fashionItemService.saveAll(jsonObject.result.itemList);
                    System.out.printf("Flushed %s items%n", jsonObject.result.itemList.size());

                }catch (Exception e) {
                    System.out.println("Could not parse json file");
                }
            }
        });
    }

    public class JsonText{
        public Result result;
    }

    public class Result{
        public List<FashionItem> itemList;
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
