package com.svrij22.main.task;

import com.svrij22.main.service.CacheService;
import com.svrij22.main.service.CrawlerService;
import com.svrij22.main.service.FashionItemService;
import com.svrij22.main.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class CacheRunner {

    @Autowired
    FashionItemService fashionItemService;

    @Autowired
    SellerService sellerService;

    @Autowired
    CrawlerService crawlerService;

    @Autowired
    CacheService cacheService;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        System.out.println("Caching");
        cacheService.refreshCache(fashionItemService.getAmountOfItems());
        fashionItemService.getAllSorted();
        fashionItemService.doSearch("air force", 500);
        fashionItemService.doSearch("air force mid", 500);
        fashionItemService.doSearch("air force high", 500);
        fashionItemService.doSearch("adidas", 500);
        fashionItemService.doSearch("nike", 500);
        fashionItemService.doSearch("human made", 500);
        fashionItemService.doSearch("fog", 500);
    }

    @Scheduled(fixedDelay = 10000)
    void schedule(){
        //cacheService.saveCache();
        if (!crawlerService.isBusy()){
            //crawlerService.crawl(sellerService.getLastUpdatedSeller().id);
        }
    }
}