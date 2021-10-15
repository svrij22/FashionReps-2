package com.svrij22.main.domain;

import net.minidev.json.annotate.JsonIgnore;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class FashionItem implements Serializable {

    @Id
    public String itemId;
    public String itemImg;
    public String itemName;
    public String translatedName;
    public String itemUrl;
    public String price;
    public String sold;
    public String stock;
    public String seller;

    @Transient
    public int position;

    public int match(String param) {
        String result = itemName.toLowerCase();
        String[] splitted = param.toLowerCase().split(" ");

        int score = 0;
        for (String split : splitted) {
            score += result.contains(split) ? 1 : 0;
        }
        if (score > 0){
            score = (int) Math.pow(getSold(), score);
        }
        return score;
    }



    public int getSold() {
        return (sold == null || sold.equals("")) ? 1 : Integer.parseInt(sold.replace("+", ""));
    }
}
