package com.putatoe.app.pojo;

import java.io.Serializable;

public class Service implements Serializable {

    Integer id;
    String name;
    String image;
    boolean isMultipleProductPurchaseSupported;


    public Service(Integer id, String name, String image, boolean isMultipleProductPurchaseSupported) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isMultipleProductPurchaseSupported = isMultipleProductPurchaseSupported;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getIsMultipleProductPurchaseSupported() {
        return isMultipleProductPurchaseSupported;
    }

    public void setIsMultipleProductPurchaseSupported(boolean multipleProductPurchaseSupported) {
        isMultipleProductPurchaseSupported = multipleProductPurchaseSupported;
    }
}
