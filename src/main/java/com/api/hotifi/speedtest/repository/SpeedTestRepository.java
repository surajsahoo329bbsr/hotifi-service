package com.api.hotifi.speedtest.repository;

import com.api.hotifi.speedtest.entity.SpeedTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeedTestRepository extends PagingAndSortingRepository<SpeedTest, Long> {

    @Query(value = "SELECT * FROM speed_test WHERE user_id = ?1", nativeQuery = true)
    List<SpeedTest> findSpeedTestsByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM speed_test WHERE user_id in :userIds", nativeQuery = true)
    List<SpeedTest> findSpeedTestsByUserIds(@Param("userIds") List<Long> userIds, Pageable pageable);

    @Query(value = "SELECT * FROM speed_test WHERE user_id = ?1 AND pin_code = ?2 AND network_provider != 'WIFI' ORDER BY 1 DESC LIMIT 1", nativeQuery = true)
    SpeedTest findLatestNonWifiSpeedTest(Long userId, String pinCode);

    @Query(value = "SELECT * FROM speed_test WHERE user_id = ?1 AND pin_code = ?2 AND network_provider = 'WIFI' ORDER BY 1 DESC LIMIT 1", nativeQuery = true)
    SpeedTest findLatestWifiSpeedTest(Long userId, String pinCode);

}
