package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Device;
import com.api.hotifi.identity.web.request.DeviceRequest;

public interface IDeviceService {

    void createDevice(DeviceRequest deviceRequest);

    Device getDeviceById(Long id);

    void updateDevice(Long id);

    void deleteDevice(Long id);

}
