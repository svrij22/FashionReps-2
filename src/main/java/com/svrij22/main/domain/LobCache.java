package com.svrij22.main.domain;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

public class LobCache {

    public String key;
    public int version;
    public List<String> fashionItemIds;
}
