package com.putatoe.app.pojo;

import java.io.Serializable;

public class Product implements Serializable {

    Integer id;
    String name;
    String price;
    String quantity;
    String discount;
    String preDiscountPrice;
    int buyQuantity;

    public Product(Integer id, String name, String price, String quantity, String discount, String preDiscountPrice, int buyQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.preDiscountPrice = preDiscountPrice;
        this.buyQuantity = buyQuantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPreDiscountPrice() {
        return preDiscountPrice;
    }

    public void setPreDiscountPrice(String preDiscountPrice) {
        this.preDiscountPrice = preDiscountPrice;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(int buyQuantity) {
        this.buyQuantity = buyQuantity;
    }
}
