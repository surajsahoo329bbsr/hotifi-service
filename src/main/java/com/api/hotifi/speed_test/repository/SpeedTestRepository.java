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

    @Query(value = "SELECT * FROM speed_test s WHERE s.user_id = ?1 AND s.pin_code = ?2 AND s.network_name != 'WIFI' ORDER BY 1 DESC LIMIT 1", nativeQuery = true)
    SpeedTest findLatestNonWifiSpeedTest(Long userId, String pinCode);

    @Query(value = "SELECT * FROM speed_test s WHERE s.user_id = ?1 AND s.pin_code = ?2 AND s.network_name = 'WIFI' ORDER BY 1 DESC LIMIT 1", nativeQuery = true)
    SpeedTest findLatestWifiSpeedTest(Long userId, String pinCode);

}
