package com.api.hotifi.identity.repository;

import com.api.hotifi.identity.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    @Query(value = "SELECT * FROM user_status u WHERE u.user_id = ?1", nativeQuery = true)
    List<UserStatus> findUserStatusByUserId(Long userId);
}
