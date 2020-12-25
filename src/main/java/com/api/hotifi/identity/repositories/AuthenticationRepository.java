package com.api.hotifi.identity.repositories;

import com.api.hotifi.identity.entities.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
    Authentication findByEmail(String email);
}
