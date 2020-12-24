package com.api.hotifi.session.repository;

import com.api.hotifi.session.entity.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends PagingAndSortingRepository<Session, Long> {

    @Query(value = "SELECT * FROM session WHERE id in :ids", nativeQuery = true)
    List<Session> findAllSessionsById(@Param("ids") List<Long> sessionIds, Pageable pageable);

    //TODO SQL Query
    @Query(value = "SELECT * FROM session WHERE speed_test_id in :speed_test_ids AND end_time == NULL ORDER BY 1 DESC", nativeQuery = true)
    List<Session> findActiveSessionsBySpeedTestIds(@Param("speed_test_ids") List<Long> speedTestIds, Pageable pageable);

}
