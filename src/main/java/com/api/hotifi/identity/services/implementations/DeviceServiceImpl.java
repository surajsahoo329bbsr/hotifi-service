package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.identity.entities.Device;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.DeviceErrorCodes;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.repositories.DeviceRepository;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.services.interfaces.IDeviceService;
import com.api.hotifi.identity.web.request.DeviceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class DeviceServiceImpl implements IDeviceService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    public DeviceServiceImpl(UserRepository userRepository, DeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    @Override
    public void addDevice(DeviceRequest deviceRequest) {
        User user = userRepository.findById(deviceRequest.getUserId()).orElse(null);
        if (user == null) throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        try {
            Set<User> users = new HashSet<>();
            users.add(user);
            Device device = new Device();

            //Set up device attributes
            device.setName(deviceRequest.getDeviceName());
            device.setAndroidId(deviceRequest.getAndroidId());
            device.setToken(deviceRequest.getToken());
            device.setUsers(users);

            //Many to many mapping starts...
            Set<Device> devices = user.getUserDevices();
            boolean isDeviceAdded = devices.add(device);

            if (isDeviceAdded) {
                user.setUserDevices(devices);
                users.add(user);
                userRepository.save(user);
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Data Integrity Error occured", e);
            throw new HotifiException(DeviceErrorCodes.DEVICE_ALREADY_ADDED);
        } catch (Exception e) {
            log.error("Error occured", e);
            throw new HotifiException(DeviceErrorCodes.UNEXPECTED_DEVICE_ERROR);
        }
    }

    @Transactional
    @Override
    public Device getDeviceByAndroidId(String androidId) {
        return deviceRepository.findByAndroidId(androidId);
    }

    @Transactional
    @Override
    public void updateDevice(DeviceRequest deviceRequest) {
        try {
            Device device = deviceRepository.findByAndroidId(deviceRequest.getAndroidId());
            Date now = new Date(System.currentTimeMillis());
            device.setName(deviceRequest.getDeviceName());
            device.setAndroidId(deviceRequest.getAndroidId());
            device.setToken(deviceRequest.getToken());
            device.setTokenCreatedAt(now);
            deviceRepository.save(device);
        } catch (Exception e) {
            log.error("Error occured", e);
            throw new HotifiException(DeviceErrorCodes.UNEXPECTED_DEVICE_ERROR);
        }
    }

    @Transactional
    @Override
    public void deleteUserDevices(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            Set<Device> devices = user != null ? user.getUserDevices() : null;
            if (devices != null) deviceRepository.deleteInBatch(devices); //deletes all devices in Db of single user
        } catch (Exception e) {
            log.error("Error Occurred ", e);
            throw new HotifiException(DeviceErrorCodes.UNEXPECTED_DEVICE_ERROR);
        }
    }
}
