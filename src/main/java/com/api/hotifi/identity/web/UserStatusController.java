package com.api.hotifi.identity.web;

import com.api.hotifi.identity.service.IUserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

public class UserStatusController {

    @Autowired
    private IUserStatusService userStatusService;

    //delete user to be called after deletion reason is updated

    @PutMapping(path = "/activate/{id}/{activate-user}")
    public ResponseEntity<?> activateUser(@PathVariable(value = "id") Long id, @PathVariable(value = "activate-user") boolean activateUser) {
        userStatusService.activateUser(id, activateUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/freeze/{id}/{freeze-user}")
    public ResponseEntity<?> freezeUser(@PathVariable(value = "id") Long id, @PathVariable(value = "freeze-user") boolean freezeUser) {
        userStatusService.freezeUser(id, freezeUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/ban/{id}/{ban-user}")
    public ResponseEntity<?> banUser(@PathVariable(value = "id") Long id, @PathVariable(value = "ban-user") boolean banUser) {
        userStatusService.banUser(id, banUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
