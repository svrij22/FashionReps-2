package main.java.com.svrij22.main.dto;

import main.java.com.svrij22.main.domain.FashionItem;

import java.io.Serializable;
import java.util.List;

public class SearchResult implements Serializable {
    public List<FashionItem> fashionItems;
    public int page;
    public int amountOfItems;

    public SearchResult(List<FashionItem> fashionItems, int page, int amountOfItems) {
        this.fashionItems = fashionItems;
        this.page = page;
        this.amountOfItems = amountOfItems;
    }
}
