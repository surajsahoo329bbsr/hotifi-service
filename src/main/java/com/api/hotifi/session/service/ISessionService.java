package com.api.hotifi.session.service;

import com.api.hotifi.session.entity.Session;
import com.api.hotifi.session.model.Buyer;
import com.api.hotifi.session.web.request.SessionRequest;
import com.api.hotifi.session.web.response.ActiveSessionsResponse;
import com.api.hotifi.session.web.response.SessionSummaryResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface ISessionService {

    Session addSession(SessionRequest sessionRequest);

    List<ActiveSessionsResponse> getActiveSessions(Set<String> usernames);

    List<ActiveSessionsResponse> getActiveSessionsInDistrict(String postalCode);

    List<ActiveSessionsResponse> getNearbyActiveSessions(double buyerLongitude, double buyerLatitude, int nearbySessionsLimit);

    List<Buyer> getBuyers(Long sessionId, boolean isActive);

    void sendNotificationsToFinishSession(Long sessionId);

    void finishSession(Long sessionId, boolean isForceStop);

    BigDecimal calculatePaymentForDataToBeUsed(Long sessionId, int dataToBeUsed);

    SessionSummaryResponse getSessionSummary(Long sessionId);

    List<SessionSummaryResponse> getSortedSessionsByDateTime(Long userId, int page, int size, boolean isDescending);

    List<SessionSummaryResponse> getSortedSessionsByDataShared(Long userId, int page, int size, boolean isDescending);
}