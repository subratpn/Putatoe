package com.putatoe.app.pojo;

public class BookingTime {

    Integer id;
    String time;
    Boolean isSelected;

    public BookingTime(Integer id, String time, Boolean isSelected) {
        this.id = id;
        this.time = time;
        this.isSelected = isSelected;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
