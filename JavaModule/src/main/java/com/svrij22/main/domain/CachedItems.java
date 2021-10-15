package main.java.com.svrij22.main.domain;

import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CachedItems implements Serializable {

    public String key;
    public int version;
    public List<Integer> fashionItemList;
    public HashMap<String, List<Integer>> fashionItemDict;
    public int matchedItems;

    public CachedItems(String key, int version, List<FashionItem> fashionItemList, int matchedItems) {
        this.key = key;
        this.version = version;
        this.fashionItemList = fashionItemList
                .stream()
                .map(item -> item.position)
                .collect(Collectors.toList());

        /* For filtering sellers*/
        fashionItemList.forEach(item -> {

            String seller = item.seller;
            int position = item.position;

            if (!fashionItemDict.containsKey(seller))
                fashionItemDict.put(seller, new ArrayList<>());

            fashionItemDict.get(seller).add(position);

        });
        this.matchedItems = matchedItems;
    }
}