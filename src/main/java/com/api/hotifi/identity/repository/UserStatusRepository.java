package com.api.hotifi.identity.repository;

import com.api.hotifi.identity.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {

    @Query("SELECT us FROM UserStatus us WHERE us.userId = :userId")
    List<UserStatus> findUserStatusByUserId(@Param("userId") Long userId);
}
