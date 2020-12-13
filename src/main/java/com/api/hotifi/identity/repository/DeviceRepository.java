package com.api.hotifi.identity.repository;

import com.api.hotifi.identity.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    //Required for CRUD Operations. Do not delete.
}
