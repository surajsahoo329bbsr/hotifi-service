package com.api.hotifi.offer.web.responses;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class OfferResponse {

    private String name;

    private String description;

    private String promoCode;

    private int discountPercentage;

    private int maximumDiscountAmount;

    private int minimumReferrals;

    private String offerType;

    private String terms;

    private Date startsAt;

    private Date expiresAt;

}
