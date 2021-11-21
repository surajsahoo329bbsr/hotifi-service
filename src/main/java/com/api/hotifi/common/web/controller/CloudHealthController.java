package com.api.hotifi.common.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/cloud-health")
public class CloudHealthController {

    @GetMapping(path = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkCloudHealth() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
