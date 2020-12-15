package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Device;
import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.repository.DeviceRepository;
import com.api.hotifi.identity.repository.UserRepository;
import com.api.hotifi.identity.web.request.DeviceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class DeviceServiceImpl implements IDeviceService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    @Override
    public void addDevice(DeviceRequest deviceRequest) {
        try {
            User user = userRepository.getOne(deviceRequest.getUserId());
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
        } catch (Exception e) {
            log.error("Error occured", e);
        }
    }

    @Transactional
    @Override
    public Device getDeviceByAndroidId(String androidId) {
        try {
            //since we need data of device only, user's data should not be shown for security
            //add logic for removing part of json
            return deviceRepository.findByAndroidId(androidId);
        } catch (Exception e) {
            log.error("Error occured", e);
        }
        return null;
    }

    @Transactional
    @Override
    public void updateDevice(DeviceRequest deviceRequest) {
        try {
            Optional<User> optionalUsers = userRepository.findById(deviceRequest.getUserId());
            Device device = deviceRepository.findByAndroidId(deviceRequest.getAndroidId());
            Set<User> users = optionalUsers.map(Collections::singleton).orElse(Collections.emptySet());
            Date now = new Date(System.currentTimeMillis());
            //Device model name
            device.setName(deviceRequest.getDeviceName());
            device.setAndroidId(deviceRequest.getAndroidId());
            device.setToken(deviceRequest.getToken());
            device.setTokenCreatedAt(now);
            device.setUsers(users);
            deviceRepository.save(device);
        } catch (DataIntegrityViolationException e) {
            log.error("Data Integrity Error occured", e);
        } catch (Exception e) {
            log.error("Error occured", e);
        }
    }

    @Transactional
    @Override
    public void deleteUserDevices(Long userId) {
        try {
            User user = userRepository.getOne(userId);
            Set<Device> devices = user.getUserDevices();
            //deletes all devices in Db of single user
            deviceRepository.deleteInBatch(devices);
        } catch (Exception e) {
            log.error("Error Occurred ", e);
        }
    }
}
