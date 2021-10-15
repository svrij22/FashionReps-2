package main.java.com.svrij22.main.service;
import main.java.com.svrij22.main.data.FashionItemRepository;
import main.java.com.svrij22.main.domain.FashionItem;
import main.java.com.svrij22.main.dto.SearchResult;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FashionItemService {

    FashionItemRepository itemRepository;
    CacheService cacheService;
    int amountOfItems;
    public static List<FashionItem> fashionItemsInMemory = new ArrayList<>();

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
            fashionItemsInMemory = itemRepository.findAll();

            //Set each position
            for(var pos = 0; pos < fashionItemsInMemory.size(); pos ++){
                FashionItem fashionItem = fashionItemsInMemory.get(pos);
                fashionItem.position = pos;
            }
            amountOfItems = fashionItemsInMemory.size();
        }
        System.out.println("Saved items in memory");
        return fashionItemsInMemory;
    }

    public SearchResult getAllSorted(int page) {

        String cacheName = "@@@all.page" + page;

        //Has cache?
        if (cacheService.hasCache(cacheName)){
            return getSearchResultForCache(cacheName, page);
        }

        //Find all and sort
        List<FashionItem> allItemsOrdered = getAll()
                .stream()
                .filter(fashionItem -> Double.parseDouble(fashionItem.price) > 12) //Price filter
                .sorted(Comparator.comparingInt(FashionItem::getSold))
                .collect(Collectors.toList());

        /*Get amount of matched items*/
        int matchedItems = allItemsOrdered.size();

        /*Reverse*/
        Collections.reverse(allItemsOrdered);

        /*Order*/
        allItemsOrdered = allItemsOrdered.stream().skip(500L * page).limit(500).collect(Collectors.toList());

        /*Cache*/
        cacheService.cacheItems(amountOfItems, cacheName, allItemsOrdered, matchedItems);

        //Return
        return new SearchResult(allItemsOrdered, page, amountOfItems);
    }

    public SearchResult getSearchResultForCache(String param, int page){
        List<FashionItem> fashionItems = cacheService.getFashionItemsByCacheKey(param);
        int matchedItems = cacheService.getAmountOfMatchedItemsForCache(param);
        return new SearchResult(fashionItems, page, matchedItems);
    }

    public int getAmountOfItems(){
        return getAll().size();
    }

    public SearchResult doSearch(String param, int page) {

        String cacheName = param + ".page" + page;

        //Has cache?
        if (cacheService.hasCache(cacheName)){
            getSearchResultForCache(cacheName, page);
        }

        //If search param too small
        if (param.length() < 2){
            return getAllSorted(page);
        }

        //Get all items
        List<FashionItem> fashionItemList = getAll();

        /*Do filter*/
        List<FashionItem> filteredItems = fashionItemList
                .stream()
                .filter(fashionItem -> fashionItem.match(param) > 0)
                .sorted(Comparator.comparingInt(fashionItem -> fashionItem.match(param)))
                .collect(Collectors.toList());

        /*Reverse*/
        Collections.reverse(filteredItems);

        /*Get amount of matched items*/
        int matchedItems = filteredItems.size();

        /*Filter for each*/
        filteredItems = filteredItems.stream().skip(500L * page).limit(500).collect(Collectors.toList());

        /*Cache items*/
        cacheService.cacheItems(amountOfItems, cacheName, filteredItems, matchedItems);

        /*Build search result*/
        return new SearchResult(filteredItems, page, matchedItems);
    }

    public List<FashionItem> getAllForSellers(Set<String> sellerIds) {
        List<FashionItem> fashionItemList = new ArrayList<>();
        for (String id: sellerIds) {
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
