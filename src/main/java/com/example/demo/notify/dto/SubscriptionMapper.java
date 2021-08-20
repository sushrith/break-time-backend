package com.example.demo.notify.dto;

public class SubscriptionMapper {
    private Subscription subscription;
    private String username;
    private String schedule;

    public SubscriptionMapper() {
    }

    public SubscriptionMapper(Subscription subscription, String username, String schedule) {
        this.subscription = subscription;
        this.username = username;
        this.schedule = schedule;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "SubscriptionMapper{" +
                "subscription=" + subscription +
                ", username='" + username + '\'' +
                ", schedule='" + schedule + '\'' +
                '}';
    }
}

