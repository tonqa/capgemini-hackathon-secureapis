package com.example;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private static final String template = "Hello, %s!";

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping("/products")
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<Product>();
        productRepository.findAll().forEach((item) -> {
        	products.add(item);
        });

        return products;
    }

    @RequestMapping(method= RequestMethod.POST, value = "/products")
    public void createProduct(@RequestBody Product product) {
    	productRepository.save(product);
    }


}