package com.api.hotifi.speed_test.repository;

import com.api.hotifi.speed_test.entity.SpeedTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeedTestRepository extends PagingAndSortingRepository<SpeedTest, Long> {
    @Query(value = "SELECT * FROM speed_test s WHERE s.user_id = ?1", nativeQuery = true)
    List<SpeedTest> findSpeedTestsByUserId(Long userId, Pageable pageable);
}
