package com.api.hotifi.offer.repositories;

import com.api.hotifi.offer.entities.Referrer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferrerRepository extends JpaRepository<Referrer, Long> {

    @Query(value = "select * from referrer where user_id = ?1 order by 1 desc limit 1",  nativeQuery = true)
    Referrer findLatestReferralByUserId(Long userId);

}
