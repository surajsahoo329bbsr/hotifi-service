package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.Device;
import com.api.hotifi.identity.web.request.DeviceRequest;

public interface IDeviceService {

    Device getDeviceByAndroidId(String androidId);

    void addDevice(DeviceRequest deviceRequest);

    void updateDevice(DeviceRequest deviceRequest);

    void deleteUserDevices(Long userId);

}
