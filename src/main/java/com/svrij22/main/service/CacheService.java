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

    List<CachedItems> cachedItems = new ArrayList<>();

    public void cacheItems(int amountOfItems, String cacheKey, List<FashionItem> items, int matchedItems){
        System.out.println("Cached " + cacheKey);

        CachedItems cachedItem = new CachedItems(cacheKey,
                amountOfItems,
                items.stream().map(item -> item.position).collect(Collectors.toList()),
                matchedItems);

        cachedItems.add(cachedItem);

        saveFile(cachedItem);
    }

    public List<FashionItem> getCache(String key, List<FashionItem> fashionItemsInMemory){
        CachedItems cItem = cachedItems
                .stream()
                .filter(c -> c.key.equals(key))
                .findFirst()
                .get();

        return cItem.fashionItemList.stream().map(fashionItemsInMemory::get).collect(Collectors.toList());
    }

    public int getAmountOfMatchedItemsForCache(String cacheName) {
        CachedItems cItem = cachedItems
                .stream()
                .filter(c -> c.key.equals(cacheName))
                .findFirst()
                .get();

        return cItem.matchedItems;
    }

    public boolean hasCache(String key) {
        return cachedItems
                .stream()
                .anyMatch(cachedItems1 -> cachedItems1.key.equals(key));
    }

    public void refreshCache(int amountOfItems){
        try{
            cachedItems = getFiles(amountOfItems);
        }catch (Exception ignored) {}
        System.out.println(String.format("Loaded %s items", cachedItems.size()));
    }

    public List<CachedItems> getFiles(int version) throws IOException {

        List<CachedItems> returnCache = new ArrayList<>();
        File dir = new File("tmp/");
        dir.mkdirs();

        List<Path> allFiles = Files.list(Paths.get(dir.getAbsolutePath()))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        allFiles.forEach(path -> {
            try {
                FileInputStream fileIn = new FileInputStream(path.toFile());
                String test = path.toFile().getAbsolutePath();
                ObjectInputStream in = new ObjectInputStream(fileIn);
                CachedItems cItem = (CachedItems) in.readObject();
                in.close();
                fileIn.close();
                if (cItem.version != version){
                    Files.delete(path);
                }else{
                    returnCache.add(cItem);
                }
            } catch (Exception e){
                e.printStackTrace();};
        });

        return returnCache;
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