package com.svrij22.main.service;

import com.svrij22.main.data.SellerRepository;
import com.svrij22.main.domain.Seller;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SellerService {

    SellerRepository sellerRepository;
    FashionItemService fashionItemService;
    FetchInjector fetchInjector;

    public SellerService(SellerRepository sellerRepository, FashionItemService fashionItemService, FetchInjector fetchInjector) {
        this.sellerRepository = sellerRepository;
        this.fashionItemService = fashionItemService;
        this.fetchInjector = fetchInjector;
    }

    public List<Seller> getAll(){
        List<Seller> allSellers = sellerRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(s -> s.itemsAmount)).collect(Collectors.toList());

        Collections.reverse(allSellers);
        return allSellers;
    }

    public List<Seller> addSeller(String name, String id) {
        sellerRepository.save(new Seller(id, name));
        return getAll();
    }

    public Seller getLastUpdatedSeller(){
        List<Seller> sellers = getAll();

        List<Seller> nonIndexedSellers = sellers.stream().filter(seller -> (seller.lastUpdated == null)).collect(Collectors.toList());

        if (nonIndexedSellers.size() > 0){
            return nonIndexedSellers.stream().findAny().get();
        }

        AtomicReference<Seller> oldestIndexedSeller = null;
        AtomicReference<LocalDateTime> earlierDate = new AtomicReference<>(LocalDateTime.now());

        sellers.forEach(seller -> {
            if (seller.lastUpdated != null && seller.lastUpdated.isBefore(earlierDate.get())){
                oldestIndexedSeller.set(seller);
               earlierDate.set(seller.lastUpdated);
            }
        });

        return oldestIndexedSeller.get();

    }

    public void updateOldestSeller(){
        Seller lastUpdated = getLastUpdatedSeller();
        fetchInjector.fetch(lastUpdated.id);
        updateSingleSeller(lastUpdated.id);
    }

    public void updateSingleSeller(String sellerid){
        Optional<Seller> seller = sellerRepository.findById(sellerid);
        seller.ifPresent(this::updateSingleSeller);
    }

    public void updateSingleSeller(Seller seller){
        seller.itemsAmount = fashionItemService.getAllForSellers(Collections.singleton(seller.id)).size();
        seller.lastUpdated = LocalDateTime.now();
        System.out.println("Updated seller "+ seller.id);
        sellerRepository.save(seller);
    }

    public void updateSellerAmounts(){
        sellerRepository.findAll().forEach(s -> {
            s.itemsAmount = fashionItemService.getAllForSellers(Collections.singleton(s.id)).size();
            sellerRepository.save(s);
        });
    }

    public List<Seller> removeSeller(String id) {
        Optional<Seller> seller = sellerRepository.findById(id);
        seller.ifPresent(value -> sellerRepository.delete(value));
        return getAll();
    }
}
