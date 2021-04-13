package com.api.hotifi.payment.processor.razorpay.codes;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

/*
    Payment Life Cycle#
    Created#
    A payment is created when the customer fills up and submits the payment information and it is sent to Razorpay. No processing has been done on the payment at this stage.

    Authorized#
    Authorization is performed when customer's payment details are successfully authenticated by the bank. The money is deducted from the customer’s account by Razorpay, but will not be settled to your account until the payment is captured, either manually or automatically.

    There can be scenarios when a payment is interrupted by external factors such as network issues or technical errors at customer's or bank's end. In such cases, money may get debited from the customer's bank account but Razorpay does not receive a payment status from the bank. This is termed as Late Authorization and you can learn more about how to handle this at your end.

    Captured#
    When the status of the payment is changed to captured, the authorized payment is verified as complete by Razorpay. After the capture, the amount is settled to your account as per the settlement schedule of the bank. The captured amount must be the same as the authorized amount. Any authorization not followed by a capture within 5 days is automatically voided and the amount is refunded to the customer.

    Refunded#
    You can refund the payments that have been successfully captured at your end. The amount is sent back to the customer's account.

    Failed#
    Any unsuccessful transaction is marked as failed and the customer will have to retry the payment.
    */
public enum PaymentStatusCodes {

    CREATED,
    FAILED,
    AUTHORIZED,
    CAPTURED,
    REFUNDED;

    private static final Map<Integer, PaymentStatusCodes> razorpayStatusCodes = new TreeMap<>();

    private static final int START_VALUE = BusinessConfigurations.RAZORPAY_PAYMENT_STATUS_START_VALUE_CODE;

    static {
        IntStream.range(0, values().length).forEach(i -> {
            values()[i].value = START_VALUE + i;
            razorpayStatusCodes.put(values()[i].value, values()[i]);
        });
    }

    private int value;

    public static PaymentStatusCodes fromInt(int i) {
        return razorpayStatusCodes.get(i);
    }

    public int value() {
        return value;
    }
}
