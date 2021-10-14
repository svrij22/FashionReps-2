package com.svrij22.main.controller;
import com.svrij22.main.service.CrawlerService;
import com.svrij22.main.service.SellerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("crawler")
public class CrawlerController {

    public final CrawlerService crawlerService;
    public final SellerService sellerService;

    public CrawlerController(CrawlerService crawlerService, SellerService sellerService) {
        this.crawlerService = crawlerService;
        this.sellerService = sellerService;
    }

    @GetMapping("update")
    public void debug(@RequestParam String sellerid){
        crawlerService.crawl(sellerid);
        sellerService.updateSingleSeller(sellerid);
    }

    @GetMapping("info")
    public String info(@RequestParam String sellerid){
        sellerService.updateSingleSeller(sellerid);
        return "test";
    }
}
