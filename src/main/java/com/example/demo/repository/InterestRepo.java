package com.example.demo.repository;
import java.util.List;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.Interest;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface InterestRepo extends JpaRepository<Interest, Integer> {

    @Query("select i from Interest i where i.id=?1")
    Interest findByInterestId(int id);

}