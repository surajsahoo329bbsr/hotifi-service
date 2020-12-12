package com.api.hotifi.identity.repository;

import com.api.hotifi.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByAuthenticationId(Long authenticationId);
    User findByUsername(String username);
}
