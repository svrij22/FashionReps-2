package com.svrij22.main.service;

import com.svrij22.main.domain.CachedItems;
import com.svrij22.main.domain.FashionItem;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CacheService {

    /*Memory cached items*/
    List<CachedItems> cachedItems = new ArrayList<>();

    /*Save a cached item*/
    public void cacheItems(int amountOfItems, String cacheKey, List<FashionItem> items, int matchedItems){
        System.out.println("Cached " + cacheKey);

        CachedItems cachedItem = new CachedItems(cacheKey,
                amountOfItems,
                items.stream().map(item -> item.position).collect(Collectors.toList()),
                matchedItems);

        cachedItems.add(cachedItem);

        saveFile(cachedItem);
    }

    /*Get a cached item*/
    public CachedItems cacheItemFindByKey(String key){
        return cachedItems
                .stream()
                .filter(c -> c.key.equals(key))
                .findAny()
                .orElse(null);
    }

    /*Get cached item*/
    public List<FashionItem> getFashionItemsByCacheKey(String key){
        CachedItems cachedItems =  cacheItemFindByKey(key);
        return cachedItems.fashionItemList
                .stream()
                .map(FashionItemService.fashionItemsInMemory::get)
                .collect(Collectors.toList());
    }

    public int getAmountOfMatchedItemsForCache(String cacheName) {
        return cacheItemFindByKey(cacheName).matchedItems;
    }

    public boolean hasCache(String key) {
        CachedItems cachedItem = cacheItemFindByKey(key);

        if (cachedItem == null) return false;

        if (cachedItem.version != FashionItemService.fashionItemsInMemory.size()){
            cachedItems.remove(cachedItem);
            return false;
        }

        return true;
    }

    public void getFiles() {

        File dir = new File("tmp/");
        dir.mkdirs();

        List<Path> allFiles = null;
        try {
            allFiles = Files.list(Paths.get(dir.getAbsolutePath()))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert allFiles != null;

        allFiles.forEach(path -> {
            try {
                FileInputStream fileIn = new FileInputStream(path.toFile());
                String test = path.toFile().getAbsolutePath();
                ObjectInputStream in = new ObjectInputStream(fileIn);
                CachedItems cItem = (CachedItems) in.readObject();
                in.close();
                fileIn.close();
                if (cItem.version != FashionItemService.fashionItemsInMemory.size()){
                    Files.delete(path);
                }else{
                    cachedItems.add(cItem);
                }
            } catch (Exception e){
                e.printStackTrace();};
        });

        System.out.println(String.format("Loaded %s items", cachedItems.size()));
    }

    public void saveFile(CachedItems cacheItem){
        try {
            String path = "/tmp/";
            String fileName =  cacheItem.key + ".cf";

            File dir = new File("tmp/");
            dir.mkdirs();
            File tmp = new File(dir, fileName);
            tmp.createNewFile();

            String canon = tmp.getCanonicalPath();
            FileOutputStream fileOut = new FileOutputStream(tmp);
            FileDescriptor fileDescriptor = fileOut.getFD();
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(cacheItem);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in /tmp/");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}