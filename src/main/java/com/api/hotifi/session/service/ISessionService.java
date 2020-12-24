package com.api.hotifi.session.service;

import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.web.request.SessionRequest;
import com.api.hotifi.session.web.response.ActiveSessionsResponse;

import java.util.List;

public interface ISessionService {

    void addSession(SessionRequest sessionRequest);

    List<ActiveSessionsResponse> getActiveSessions(List<String> username, int pageNumber, int elements);

    List<Session> getSortedSessionsByStartTime(Long userId, int pageNumber, int elements, boolean isDescending);

    List<Session> getSortedSessionsByDataUsed(Long userId, int pageNumber, int elements, boolean isDescending);
}
