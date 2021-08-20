package com.example.demo.model;

import java.util.ArrayList;

public class User_Subscription_Payload {

    String email;
    String subscription_links;
    public User_Subscription_Payload() {

    }
    public User_Subscription_Payload(String email, String subscription_links) {
        super();
        this.email = email;
        this.subscription_links = subscription_links;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSubscription_links() {
        return subscription_links;
    }
    public void setSubscription_links(String subscription_links) {
        this.subscription_links = subscription_links;
    }

}