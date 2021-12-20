package org.xyz.concurrent.concurrent.futuretask;


public class ProductInfo {
    private String name;

    ProductInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ProductInfo loadProductInfo() {
        return new ProductInfo("test");
    }
}
