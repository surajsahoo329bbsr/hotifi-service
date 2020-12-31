package com.api.hotifi.session.web.controller;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.service.ISessionService;
import com.api.hotifi.session.web.request.SessionRequest;
import com.api.hotifi.session.web.response.ActiveSessionsResponse;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@Validated
@RestController
@Api(tags = Constants.SESSION_TAG)
@RequestMapping(path = "/session")
public class SessionController {

    @Autowired
    private ISessionService sessionService;

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSession(@RequestBody @Validated SessionRequest sessionRequest) {
        sessionService.addSession(sessionRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/get/active-sessions/{usernames}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveSessions(@PathVariable(value = "usernames") HashSet<String> usernames) {
        List<ActiveSessionsResponse> activeSessionsResponses = sessionService.getActiveSessions(usernames);
        return new ResponseEntity<>(activeSessionsResponses, HttpStatus.OK);
    }


    @GetMapping(path = "/get/start-time/{user-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedSessionsByStartTime(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                          @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                          @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                          @PathVariable(value = "is-descending") boolean isDescending) {
        List<Session> sessions = sessionService.getSortedSessionsByStartTime(userId, page, size, isDescending);
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }

    @GetMapping(path = "/get/data-used/{user-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSortedSessionsByDataUsed(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId,
                                                         @PathVariable(value = "page") @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                         @PathVariable(value = "size") @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                         @PathVariable(value = "is-descending") boolean isDescending) {
        List<Session> sessions = sessionService.getSortedSessionsByDataUsed(userId, page, size, isDescending);
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }
}
