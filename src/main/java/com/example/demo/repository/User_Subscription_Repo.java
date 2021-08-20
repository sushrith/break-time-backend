package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.example.demo.model.User_Subscription;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface User_Subscription_Repo extends CrudRepository<User_Subscription, Integer> {
    @Query("select s.subscription_link from User_Subscription s where s.user_id=?1")
    List<String> findSubscriptionsById(int x);

    @Query("select s.user_id from User_Subscription s where s.subscription_link=?1")
    int findIdByLink(String x);

    @Query("select s from User_Subscription s where s.user_id=?1")
    List<User_Subscription> findSubscriptionsByIdUser(int x);


}