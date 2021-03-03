package com.api.hotifi.payment.web.request;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.validators.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PurchaseRequest {

    @NotBlank(message = "{payment.id.blank}")
    @Length(max = 255, message = "{payment.id.invalid}")
    private String paymentId;

    @Range(min = 1, message = "{session.id.invalid}")
    private Long sessionId;

    @Range(min = 1, message = "{status.id.invalid}")
    private Long buyerId;

    @Length(max = 20, message = "{mac.address.invalid}")
    private String macAddress;

    @Length(max = 45, message = "{mac.address.invalid}")
    private String ipAddress;

    @PaymentMethod
    private String paymentMethod;

    @Range(min = Constants.MINIMUM_SELLING_DATA_MB, max = Constants.MAXIMUM_SELLING_DATA_MB, message = "{data.range.invalid}")
    private int data;
}
