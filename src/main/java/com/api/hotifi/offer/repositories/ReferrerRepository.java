package com.api.hotifi.offer.repositories;

import com.api.hotifi.offer.entities.Referrer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferrerRepository extends JpaRepository<Referrer, Long> {
}
