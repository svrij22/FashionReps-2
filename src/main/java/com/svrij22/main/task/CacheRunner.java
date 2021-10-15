package com.svrij22.main.task;

import com.svrij22.main.service.CacheService;
import com.svrij22.main.service.FashionItemService;
import com.svrij22.main.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class CacheRunner {

    @Autowired
    FashionItemService fashionItemService;

    @Autowired
    SellerService sellerService;

    @Autowired
    CacheService cacheService;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        System.out.println("Caching");
        fashionItemService.getAmountOfItems();
        //cacheService.getFiles();
        //fashionItemService.getAllSorted(0);
        //fashionItemService.doSearch("air force", 0);
        //fashionItemService.doSearch("air force mid", 0);
        //fashionItemService.doSearch("air force high", 0);
        //fashionItemService.doSearch("adidas", 0);
        //fashionItemService.doSearch("nike", 0);
        //fashionItemService.doSearch("human made", 0);
        //fashionItemService.doSearch("fog", 0);

        sellerService.updateSellerAmounts();
    }

    @Scheduled(fixedDelay = 50000)
    void schedule(){
        //sellerService.updateOldestSeller();
    }
}