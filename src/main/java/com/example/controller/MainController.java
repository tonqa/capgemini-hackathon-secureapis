package com.example.controller;

import com.example.model.Product;
import com.example.model.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Example controller to test security calls
 */
@RestController
public class MainController {

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping(method=RequestMethod.GET, value="/products")
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<Product>();
        productRepository.findAll().forEach((item) -> {
        	products.add(item);
        });

        return products;
    }

    @RequestMapping(method=RequestMethod.POST, value = "/products")
    public void createProduct(@RequestBody Product product) {
    	productRepository.save(product);
    }

    @RequestMapping(value = {"/user", "/me"}, method = RequestMethod.POST)
    public ResponseEntity<?> user(Principal principal) {
        return ResponseEntity.ok(principal);
    }
}