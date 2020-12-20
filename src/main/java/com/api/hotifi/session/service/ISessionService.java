package com.api.hotifi.session.service;

import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.web.request.SessionRequest;

import java.util.List;

public interface ISessionService {

    void addSession(SessionRequest sessionRequest);

    List<Session> getActiveSessions(List<String> username, int pageNumber, int elements);

    List<Session> sortAllSessionsByStartTime(Long userId, int pageNumber, int elements, boolean isDescending);

    List<Session> sortAllSessionsByDataUsed(Long userId, int pageNumber, int elements, boolean isDescending);
}
