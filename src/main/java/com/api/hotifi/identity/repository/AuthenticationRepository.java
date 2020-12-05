package com.api.hotifi.identity.repository;

import com.api.hotifi.identity.entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
    Authentication findByEmail(String email);
    Authentication findByPhone(String phone);
}
