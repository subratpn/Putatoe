package com.putatoe.app.pojo;

import android.content.Intent;

import java.util.Date;
import java.util.List;

public class BookingDate {

    Integer id;
    String date;
    Boolean isSelected;

    public BookingDate(Integer id, String date, Boolean isSelected) {
        this.id = id;
        this.date = date;
        this.isSelected = isSelected;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
