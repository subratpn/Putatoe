package com.putatoe.app.pojo;

public class Review {

    Integer id;
    String review;
    String customerName;
    String rating;


    public Review(Integer id, String review, String customerName, String rating) {
        this.id = id;
        this.review = review;
        this.customerName = customerName;
        this.rating = rating;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
