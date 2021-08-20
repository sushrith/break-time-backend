package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="subscription")
public class User_Subscription {
    @Id
    @Column
    @GeneratedValue
    private int subscription_id;

    @Column
    private int user_id;

    @Column(length = 8192)
    private String subscription_link;

    public User_Subscription( int user_id, String subscription_) {
        super();
        this.user_id = user_id;
        this.subscription_link = subscription_;
    }
    public User_Subscription()
    {

    }
    public int getSubscription_id() {
        return subscription_id;
    }
    public void setSubscription_id(int subscription_id) {
        this.subscription_id = subscription_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public String getSubscription_link() {
        return subscription_link;
    }
    public void setSubscription_link(String subscription_link) {
        this.subscription_link = subscription_link;
    }




}
