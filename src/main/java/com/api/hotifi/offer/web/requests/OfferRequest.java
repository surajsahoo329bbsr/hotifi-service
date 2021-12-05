package com.api.hotifi.offer.web.requests;

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

@Getter
@Setter
@AllArgsConstructor
public class OfferRequest {

    @NotBlank(message = "{offer.request.name.blank}")
    @Length(max = 100, message = "{offer.request.name.invalid}")
    private String name;

    @NotBlank(message = "{offer.request.description.blank}")
    @Length(max = 255, message = "{offer.request.description.invalid}")
    private String description;

    @Length(max = 12, message = "{offer.request.description.invalid}")
    private String promoCode;

    @Range(min = BusinessConfigurations.MINIMUM_PRICE_BUDGET_PER_OFFER, message = "{offer.request.minimum.price.budget.invalid}")
    private BigDecimal priceBudget;

    @Range(min = 0, max = 100, message = "{offer.request.commission.percentage.invalid}")
    private int commissionPercentage;

    @Range(min = 0, max = 100, message = "{offer.request.discount.percentage.invalid}")
    private int discountPercentage;

    @Range(min = 0, max = 100, message = "{offer.request.maximum.discount.amount.invalid}")
    private int maximumDiscountAmount;

    @Range(min = 0, message = "{offer.request.minimum.referrals.invalid}")
    private int minimumReferrals;

    @Column(nullable = false)
    private String offerType;

    private String terms;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;
}
