package com.api.hotifi.session.repository;

import com.api.hotifi.session.entity.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SessionRepository extends PagingAndSortingRepository<Session, Long> {

    @Query(value = "SELECT * FROM session WHERE id in :ids", nativeQuery = true)
    List<Session> findAllSessionsById(@Param("ids") List<Long> sessionIds, Pageable pageable);

    @Query(value = "SELECT * FROM session WHERE speed_test_id in :speed_test_ids", nativeQuery = true)
    List<Session> findSessionsBySpeedTestIds(@Param("speed_test_ids") List<Long> speedTestIds);

    @Modifying
    @Query(value = "UPDATE session SET finished_at = :finished_at WHERE speed_test_id in :speed_test_ids AND finished_at IS NULL", nativeQuery = true)
    void updatePreviousSessionsFinishTimeIfNull(@Param("speed_test_ids") List<Long> speedTestIds, @Param("finished_at") Date finishedAt);

    @Query(value = "SELECT * FROM session WHERE speed_test_id in :speed_test_ids AND finished_at IS NULL", nativeQuery = true)
    List<Session> findActiveSessionsBySpeedTestIds(@Param("speed_test_ids") List<Long> speedTestIds);

}
