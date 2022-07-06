package com.hanuman.smartagriculture.models;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class Order implements Serializable {
    @Exclude
            private String key;

    private Date orderDate;
    String orderId;
    String sellerEmail, buyerEmail,buyerName, buyerProfile,productTitle,stockTotal,orderPrice;
    boolean isCompleted = false;

    public Order(String sellerEmail, String buyerEmail, String buyerName, String buyerProfile
            ,String productTitle,String stockTotal,String orderPrice,boolean isCompleted) {
        this.orderId= UUID.randomUUID().toString();
        this.orderDate= Calendar.getInstance().getTime();
        this.sellerEmail = sellerEmail;
        this.buyerEmail = buyerEmail;
        this.buyerName = buyerName;
        this.buyerProfile = buyerProfile;
        this.productTitle = productTitle;
        this.stockTotal = stockTotal;
        this.orderPrice = orderPrice;
        this.isCompleted = isCompleted;
    }
    public Order(){
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerProfile() {
        return buyerProfile;
    }

    public void setBuyerProfile(String buyerProfile) {
        this.buyerProfile = buyerProfile;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(String stockTotal) {
        this.stockTotal = stockTotal;
    }

    @Override
    public String toString() {
        return "Order{" +
                "key='" + key + '\'' +
                ", orderId='" + orderId + '\'' +
                ", sellerEmail='" + sellerEmail + '\'' +
                ", buyerEmail='" + buyerEmail + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", buyerProfile='" + buyerProfile + '\'' +
                ", productTitle='" + productTitle + '\'' +
                ", stockTotal='" + stockTotal + '\'' +
                ", orderPrice='" + orderPrice + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
