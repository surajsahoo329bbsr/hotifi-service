package com.api.hotifi.payment.web.request;

import com.api.hotifi.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
public class PurchaseRequest {

    @NotBlank(message = "{payment.id.blank}")
    @Length(max = 255, message = "{payment.id.invalid}")
    private String paymentId;

    @Range(min = 1, message = "{session.id.invalid}")
    private Long sessionId;

    @Range(min = 0, max = Integer.MAX_VALUE, message = "{status.id.invalid}")
    private int status;

    @Range(min = 1, message = "{status.id.invalid}")
    private Long buyerId;

    @NotBlank(message = "{mac.address.blank}")
    @Length(max = 255, message = "{mac.address.invalid}")
    private String macAddress;

    @NotBlank(message = "{payment.datetime.blank}")
    private Date paymentDoneAt;

    @Range(min = Constants.MINIMUM_SELLING_DATA, max = Constants.MAXIMUM_SELLING_DATA, message = "{data.range.invalid}")
    private int data;

    @Range(min = Constants.MINIMUM_AMOUNT_PAID_INR, max = Constants.MAXIMUM_AMOUNT_PAID_INR, message = "{amont.paid.invalid.range}")
    private int amountPaid;
}
