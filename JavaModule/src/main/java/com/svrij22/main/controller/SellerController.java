package main.java.com.svrij22.main.controller;

import main.java.com.svrij22.main.domain.Seller;
import main.java.com.svrij22.main.service.SellerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sellers")
public class SellerController {

    SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public List<Seller> getAll(){
       return sellerService.getAll();
    }

    @GetMapping("/add")
    public List<Seller> addSeller(@RequestParam String name, @RequestParam String id) {
        return sellerService.addSeller(name, id);
    }

    @GetMapping("/remove")
    public List<Seller> addSeller(@RequestParam String id) {
        return sellerService.removeSeller(id);
    }
}
