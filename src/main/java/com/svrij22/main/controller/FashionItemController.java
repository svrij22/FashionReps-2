package com.svrij22.main.controller;

import com.svrij22.main.domain.FashionItem;
import com.svrij22.main.dto.SearchResult;
import com.svrij22.main.service.FashionItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("items")
public class FashionItemController {

    FashionItemService itemService;

    public FashionItemController(FashionItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public SearchResult getItems(){
       return doSearch("", 0);
    }

    @GetMapping("/search")
    public SearchResult doSearch(@RequestParam String param, @RequestParam int page) {
        return itemService.doSearch(param, page);
    }

    @GetMapping("/translate")
    public void doTranslate() {
        itemService.RunTranslations();
    }
}
