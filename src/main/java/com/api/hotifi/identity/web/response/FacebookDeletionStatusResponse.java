package com.api.hotifi.identity.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@AllArgsConstructor
public class FacebookDeletionStatusResponse {

    @NotBlank(message = "{facebook.id.blank}")
    private String facebookId;

    @NotBlank(message = "{facebook.deletion.request.timestamp.blank}")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletionRequestedAt;

    private boolean isDeleted;

    @NotBlank(message = "{facebook.deletion.reason.blank}")
    private String reason;

    @NotBlank(message = "{privacy.url.blank}")
    private String privacyUrl;

}
