package com.svrij22.main.service;
import com.svrij22.main.data.FashionItemRepository;
import com.svrij22.main.domain.FashionItem;
import com.svrij22.main.domain.Seller;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FashionItemService {

    FashionItemRepository itemRepository;
    CacheService cacheService;
    int amountOfItems;
    List<FashionItem> fashionItemsInMemory = new ArrayList<>();

    public FashionItemService(FashionItemRepository itemRepository, CacheService cacheService) {
        this.itemRepository = itemRepository;
        this.cacheService = cacheService;
    }

    public void saveAll(List<FashionItem> itemList) {
        itemRepository.saveAll(itemList);
        amountOfItems = 0;
    }

    public List<FashionItem> getAll() {
        if (amountOfItems == 0){
            fashionItemsInMemory =  itemRepository.findAll();

            //Set each position
            for(var pos = 0; pos < fashionItemsInMemory.size(); pos ++){
                FashionItem fashionItem = fashionItemsInMemory.get(pos);
                fashionItem.position = pos;
            }
            amountOfItems = fashionItemsInMemory.size();
        }
        return fashionItemsInMemory;
    }

    public List<FashionItem> getAllSorted() {
        //Has cache?
        if (cacheService.hasCache("@@@all"))
            return cacheService.getCache("@@@all", fashionItemsInMemory);

        //Find all and sort
        List<FashionItem> allItemsOrdered = getAll()
                .stream()
                .sorted(Comparator.comparingInt(FashionItem::getSold))
                .collect(Collectors.toList());
        Collections.reverse(allItemsOrdered);
        allItemsOrdered = allItemsOrdered.stream().limit(500).collect(Collectors.toList());

        cacheService.cacheItems(amountOfItems, "@@@all", allItemsOrdered);

        //Return
        return allItemsOrdered;
    }

    public int getAmountOfItems(){
        return getAll().size();
    }

    public List<FashionItem> doSearch(String param, int limit) {

        //Has cache?
        if (cacheService.hasCache(param))
            return cacheService.getCache(param, fashionItemsInMemory);

        //Get all items
        List<FashionItem> fashionItemList = getAll();

        //If search param too small
        if (param.length() < 2){
            return  fashionItemList;
        }

        List<FashionItem> filteredItems = fashionItemList
                .stream()
                .sorted(Comparator.comparingInt(fashionItem -> fashionItem.match(param)))
                .collect(Collectors.toList());
        Collections.reverse(filteredItems);


        filteredItems = filteredItems.stream().limit(500).collect(Collectors.toList());

        cacheService.cacheItems(amountOfItems, param, filteredItems);

        return filteredItems;
    }

    public List<FashionItem> getAllForSellers(Set<String> sellersids) {
        List<FashionItem> fashionItemList = new ArrayList<>();
        for (String id: sellersids) {
            fashionItemList.addAll(itemRepository.findBySeller(id));
        }
        return fashionItemList;
    }

    public void RunTranslations(){
        List<FashionItem> fashionItemList = getAll().stream()
                .filter(fashionItem -> fashionItem.translatedName == null)
                .collect(Collectors.toList());

        fashionItemList.forEach(fashionItem -> {
            System.out.println(String.format("Translating fahion item %s", fashionItem.itemId));
            try {
                String translation = GetTranslation(fashionItem.itemName);
                if (translation != null){
                    fashionItem.translatedName = translation;
                    itemRepository.save(fashionItem);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public String GetTranslation(String originalText) throws IOException {
        URL url = new URL("https://translate.mentality.rip/translate");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String jsonInputString = String.format("{\"q\": \"%s\", \"source\": \"zh\", \"target\": \"en\", \"format\": \"text\"}", originalText);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            con.disconnect();

            System.out.println(response.toString());
            return response.toString();
        }
    }
}
