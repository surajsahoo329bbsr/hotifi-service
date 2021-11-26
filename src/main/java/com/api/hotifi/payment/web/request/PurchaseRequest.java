package com.api.hotifi.payment.web.request;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
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

    @NotBlank(message = "{order.id.blank}")
    @Length(max = 255, message = "{order.id.invalid}")
    private String orderId;

    @NotBlank(message = "{client.razorpay.signature.blank}")
    @Length(max = 2048, message = "{client.razorpay.signature.invalid")
    private String clientRazorpaySignature;

    @Range(min = 1, message = "{session.id.invalid}")
    private Long sessionId;

    @Range(min = 1, message = "{status.id.invalid}")
    private Long buyerId;

    @Length(max = 20, message = "{mac.address.invalid}")
    private String macAddress;

    @Length(max = 45, message = "{mac.address.invalid}")
    private String ipAddress;

    @Range(min = BusinessConfigurations.MINIMUM_BUYING_DATA_MB, message = "{min.data.range.invalid}")
    private int data;
}
