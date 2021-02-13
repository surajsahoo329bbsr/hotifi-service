package com.api.hotifi.session.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AmountToBePaidResponse {

    private BigDecimal amountToBePaid;

}
