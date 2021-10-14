package com.svrij22.main.domain;

import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CachedItems implements Serializable {

    public String key;
    public int version;
    public List<Integer> fashionItemList;

    public CachedItems(String key, int version, List<Integer> fashionItemList) {
        this.key = key;
        this.version = version;
        this.fashionItemList = fashionItemList;
    }
}