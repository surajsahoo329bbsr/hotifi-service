package com.api.hotifi.identity.web.controller;

import com.api.hotifi.authorization.service.ICustomerAuthorizationService;
import com.api.hotifi.authorization.utils.AuthorizationUtils;
import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.identity.entities.UserStatus;
import com.api.hotifi.identity.services.interfaces.IUserStatusService;
import com.api.hotifi.identity.web.request.UserStatusRequest;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private ICustomerAuthorizationService customerAuthorizationService;

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Add User Status Details",
            notes = "Add User Status Details",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> addUserStatus(@RequestBody @Validated UserStatusRequest userStatusRequest) {
        List<UserStatus> userStatus = userStatusService.addUserStatus(userStatusRequest);
        return new ResponseEntity<>(userStatus, HttpStatus.OK);
    }

    @GetMapping(path = "/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get User Status Details By User Id",
            notes = "Add User Status Details By User Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getUserStatusByUserId(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId) {
        List<UserStatus> userStatuses = (AuthorizationUtils.isAdministratorRole() ||
                customerAuthorizationService.isAuthorizedByUserId(userId, AuthorizationUtils.getUserToken())) ?
                userStatusService.getUserStatusByUserId(userId) : null;
        return new ResponseEntity<>(userStatuses, HttpStatus.OK);
    }

    @PutMapping(path = "/unfreeze/{user-id}")
    @ApiOperation(
            value = "Freeze/Unfreeze User Status By User Id",
            notes = "Freeze/Unfreeze User Status By User Id",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> unfreezeUser(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId) {
        userStatusService.freezeUser(userId, false);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
