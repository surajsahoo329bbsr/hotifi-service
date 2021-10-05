package com.api.hotifi.common.web.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotificationAllRequest {

    public List<Long> userIds;

    public NotificationCommonRequest notificationCommonRequest;

}
