package com.api.hotifi.identity.web;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entity.UserStatus;
import com.api.hotifi.identity.service.IUserStatusService;
import com.api.hotifi.identity.web.request.UserStatusRequest;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Api(tags = Constants.USER_STATUS_TAG)
@RequestMapping(path = "/user-status")
public class UserStatusController {

    //For Deleting/ Freezing/ Banning any user, addUserStatus should be called first to provide reason for the harsh action

    @Autowired
    private IUserStatusService userStatusService;

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUserStatus(@RequestBody @Validated UserStatusRequest userStatusRequest) {
        List<UserStatus> userStatus = userStatusService.addUserStatus(userStatusRequest);
        return new ResponseEntity<>(userStatus, HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/get/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserStatusByUserId(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId) {
        List<UserStatus> userStatuses = userStatusService.getUserStatusByUserId(userId);
        return new ResponseEntity<>(userStatuses, HttpStatus.OK);
    }

    @PutMapping(path = "/unfreeze/{user-id}")
    public ResponseEntity<?> unfreezeUser(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId) {
        userStatusService.freezeUser(userId, false);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
