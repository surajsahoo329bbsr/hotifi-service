package com.api.hotifi.payment.error;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class SellerBankAccountErrorCodes extends ErrorCodes {
    public static final ErrorCode UNEXPECTED_SELLER_BANK_ACCOUNT_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_SELLER_BANK_ACCOUNT_ERROR", SellerBankAccountErrorMessages.UNEXPECTED_SELLER_BANK_ACCOUNT_ERROR)), 500);
    public static final ErrorCode NO_ERROR_DESCRIPTION_ON_CREATION_BY_SELLER = new ErrorCode("01", Collections.singletonList(getMessage("NO_ERROR_DESCRIPTION_ON_CREATION_BY_SELLER", SellerBankAccountErrorMessages.NO_ERROR_DESCRIPTION_ON_CREATION_BY_SELLER)), 500);
    public static final ErrorCode NO_ERROR_DESCRIPTION_ON_UPDATION_BY_SELLER = new ErrorCode("02", Collections.singletonList(getMessage("NO_ERROR_DESCRIPTION_ON_UPDATION_BY_SELLER", SellerBankAccountErrorMessages.NO_ERROR_DESCRIPTION_ON_UPDATION_BY_SELLER)), 500);
    public static final ErrorCode BANK_ACCOUNT_DETAILS_ALREADY_EXISTS = new ErrorCode("03", Collections.singletonList(getMessage("BANK_ACCOUNT_DETAILS_ALREADY_EXISTS", SellerBankAccountErrorMessages.BANK_ACCOUNT_DETAILS_ALREADY_EXISTS)), 500);
}
