package com.svrij22.main.data;

import com.svrij22.main.domain.FashionItem;
import com.svrij22.main.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, String> {
}