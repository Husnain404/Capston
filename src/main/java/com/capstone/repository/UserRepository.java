package com.capstone.repository;

import com.capstone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);


    @Query("""
            select u from User u
            WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
                          LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) 
            """)
    List<User> findUserByMailOrName(@Param("search") String search);
}
