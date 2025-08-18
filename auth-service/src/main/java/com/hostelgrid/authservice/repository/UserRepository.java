package com.hostelgrid.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hostelgrid.authservice.model.User;

/**
 * Repository interface for managing User entities.
 * Provides built-in CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username to search
     * @return an Optional containing the User if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email.
     *
     * @param email the email to search
     * @return an Optional containing the User if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their phone number.
     *
     * @param phoneNumber the phone number to search
     * @return an Optional containing the User if found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Checks whether a user exists with the given email and phone number.
     *
     * @param email the email to check
     * @param phoneNumber the phone number to check
     * @return true if a user exists with both email and phone number, false otherwise
     */
    Boolean existsByEmailAndPhoneNumber(String email, String phoneNumber);

    /**
    * Checks whether a user exists with the given email OR phone number.
    *
    * @param email the email to check
    * @param phoneNumber the phone number to check
    * @return true if a user exists with either email or phone number, false otherwise
    */
     Boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);


    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
}