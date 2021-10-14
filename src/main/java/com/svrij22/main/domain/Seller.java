package com.svrij22.main.domain;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Seller implements Serializable {
    @Id
    public String id;
    public String name;
    public LocalDateTime lastUpdated;
    public int itemsAmount;
    public String returnRate;
    public String description;

    public Seller() {
    }

    public Seller(String id, String name) {
        this.id = id;
        this.name = name;
    }
}