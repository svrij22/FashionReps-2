package main.java.com.svrij22.main.data;

import main.java.com.svrij22.main.domain.FashionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface FashionItemRepository extends JpaRepository<FashionItem, Long> {
    Collection<FashionItem> findBySeller(String id);
}