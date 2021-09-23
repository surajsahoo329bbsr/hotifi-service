package com.api.hotifi.common.web.controller;

import com.api.hotifi.authorization.service.ICustomerAuthorizationService;
import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.common.processors.codes.CloudClientCodes;
import com.api.hotifi.common.services.interfaces.IEmailService;
import com.api.hotifi.common.services.interfaces.INotificationService;
import com.api.hotifi.common.web.request.NotificationCommonRequest;
import com.api.hotifi.common.web.request.NotificationRequest;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.models.EmailModel;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@Api(tags = AppConfigurations.NOTIFICATION_TAG)
@RequestMapping(path = "/notification")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private ICustomerAuthorizationService customerAuthorizationService;

    @PostMapping(path = "/admin/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Send Notification To Single User",
            notes = "Send Notification To Single User",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> sendNotificationToSingleUser(@RequestBody @Validated NotificationRequest notificationRequest) {
        notificationService.sendNotification(notificationRequest.getUserId(),
                notificationRequest.getTitle(), notificationRequest.getMessage(), CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //Below endpoint added to test emails in jar format
    /*@GetMapping(path = "/test/email", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Test Email From Jar",
            notes = "Test Email From Jar",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> sendTestEmail() {
        User user = new User();
        user.setFirstName("Suraj");
        EmailModel emailModel = new EmailModel();
        emailModel.setToEmail("surajsahoo329bbsr@gmail.com");
        emailModel.setFromEmail(AppConfigurations.FROM_EMAIL);
        emailModel.setFromEmailPassword(AppConfigurations.FROM_EMAIL_PASSWORD);
        emailService.sendWelcomeEmail(user, emailModel);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/

    @PostMapping(path = "/admin/common/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Send Common Notification To All Users",
            notes = "Send Common Notification To All Users",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> sendNotificationToAllUsers(@RequestBody @Validated NotificationCommonRequest notificationCommonRequest) {
        notificationService.sendNotificationsToAllUsers(
                notificationCommonRequest.getTitle(), notificationCommonRequest.getMessage(),
                notificationCommonRequest.getPhotoUrl(), CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

