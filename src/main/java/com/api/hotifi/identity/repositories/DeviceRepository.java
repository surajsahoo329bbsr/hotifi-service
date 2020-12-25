package com.api.hotifi.identity.repositories;

import com.api.hotifi.identity.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Device findByAndroidId(String androidId);
}
