package com.api.hotifi.identity.repositories;

import com.api.hotifi.identity.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query(value = "SELECT * FROM role WHERE name = ?1", nativeQuery = true)
    Role findByRoleName(String name);

}
