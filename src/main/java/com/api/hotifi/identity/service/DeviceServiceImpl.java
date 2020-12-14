package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Device;
import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.repository.DeviceRepository;
import com.api.hotifi.identity.repository.UserRepository;
import com.api.hotifi.identity.web.request.DeviceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class DeviceServiceImpl implements  IDeviceService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    @Override
    public void createDevice(DeviceRequest deviceRequest) {
        try{
            User user = userRepository.getOne(deviceRequest.getUserId());
            Set<User> users = new HashSet<>();
            users.add(user);
            Device device = new Device();
            //Device model name
            device.setName(deviceRequest.getDeviceName());
            device.setToken(deviceRequest.getToken());
            device.setUsers(users);
            deviceRepository.save(device);
        } catch (Exception e){
            log.error("Error occured", e);
        }
    }

    @Transactional
    @Override
    public Device getDeviceById(Long id) {
        try{
            return deviceRepository.getOne(id);
        } catch (Exception e){
            log.error("Error occured", e);
        }
        return null;
    }

    @Transactional
    @Override
    public void updateDevice(Long id) {

    }

    @Transactional
    @Override
    public void deleteDevice(Long id) {

    }
}
