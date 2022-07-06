package com.hanuman.smartagriculture.models;

import java.io.Serializable;

public class Sale implements Serializable {
    int totalSale=0, totalOrders=0;
    String key;

    public Sale(int totalSale, int totalOrders) {
        this.totalSale = totalSale;
        this.totalOrders = totalOrders;

    }
    public Sale(){};

    public int getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(int totalSale) {
        this.totalSale = totalSale;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "totalSale=" + totalSale +
                ", totalOrders=" + totalOrders +
                '}';
    }
}
