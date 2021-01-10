package com.api.hotifi.payment.web.request;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.validators.PaymentMethod;
import com.api.hotifi.payment.validators.UpiApp;
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

    @NotBlank(message = "{mac.address.blank}")
    @Length(max = 255, message = "{mac.address.invalid}")
    private String macAddress;

    @PaymentMethod
    private String paymentMethod;

    @UpiApp
    private String upiApp;

    @Range(min = Constants.MINIMUM_SELLING_DATA, max = Constants.MAXIMUM_SELLING_DATA, message = "{data.range.invalid}")
    private int data;
}
