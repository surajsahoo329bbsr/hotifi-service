package com.api.hotifi.identity.repositories;

import com.api.hotifi.identity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByAuthenticationId(Long authenticationId);

    User findByUsername(String username);

    @Query(value = "SELECT * FROM user WHERE username IN :usernames", nativeQuery = true)
    List<User> findAllUsersByUsernames(@Param("usernames") List<String> usernames);

}
