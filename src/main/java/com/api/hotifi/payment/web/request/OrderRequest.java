package com.api.hotifi.payment.web.request;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrderRequest {

    @Range(min = 1, message = "{session.id.invalid}")
    private Long sessionId;

    @Range(min = 1, message = "{status.id.invalid}")
    private Long buyerId;

    @Range(min = BusinessConfigurations.MINIMUM_BUYING_DATA_MB, message = "{min.data.range.invalid}")
    private int data;

    @Range(min = BusinessConfigurations.MINIMUM_PURCHASE_AMOUNT, message = "{min.amount.range.invalid}")
    private BigDecimal amount;

}
