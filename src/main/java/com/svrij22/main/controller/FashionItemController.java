package com.svrij22.main.controller;

import com.svrij22.main.domain.FashionItem;
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
    public List<FashionItem> getItems(){
       return itemService.getAllSorted();
    }

    @GetMapping("/search")
    public List<FashionItem> doSearch(@RequestParam String param) {
        return itemService.doSearch(param, 100);
    }

    @GetMapping("/translate")
    public void doTranslate() {
        itemService.RunTranslations();
    }
}
