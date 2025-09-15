package com.art.artsea.repository;

import com.art.artsea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    void deleteByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.username = :username, u.phone = :phone WHERE u.userId = :userId")
    void updateUserProfile(@Param("userId") Long userId,
                           @Param("username") String username,
                           @Param("phone") Long phone);


    //  New method to get all unverified users
    List<User> findByIsVerified(int isVerified);
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'BUYER'")
    long countBuyers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'SELLER'")
    long countSellers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.isVerified = 1")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.isVerified = 0")
    long countPendingUsers();
}

