package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.error.SellerPaymentErrorCodes;
import com.api.hotifi.payment.processor.codes.SellerPaymentCodes;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.repositories.SellerReceiptRepository;
import com.api.hotifi.payment.services.interfaces.ISellerPaymentService;
import com.api.hotifi.payment.services.interfaces.ISellerReceiptService;
import com.api.hotifi.payment.utils.PaymentUtils;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Slf4j
public class SellerPaymentServiceImpl implements ISellerPaymentService {

    private final SellerPaymentRepository sellerPaymentRepository;
    private final SellerReceiptRepository sellerReceiptRepository;
    private final UserRepository userRepository;
    private final ISellerReceiptService sellerReceiptService;

    public SellerPaymentServiceImpl(SellerPaymentRepository sellerPaymentRepository, SellerReceiptRepository sellerReceiptRepository, UserRepository userRepository, ISellerReceiptService sellerReceiptService) {
        this.sellerPaymentRepository = sellerPaymentRepository;
        this.sellerReceiptRepository = sellerReceiptRepository;
        this.userRepository = userRepository;
        this.sellerReceiptService = sellerReceiptService;
    }

    //No need to implement try catch and condition checks since this method will be
    //called by PurchaseServiceImpl
    @Transactional
    @Override
    public void addSellerPayment(User seller, BigDecimal amountEarned) {
        SellerPayment sellerPayment = new SellerPayment();
        sellerPayment.setSeller(seller);
        sellerPayment.setAmountEarned(amountEarned);
        sellerPayment.setAmountPaid(BigDecimal.ZERO);
        sellerPaymentRepository.save(sellerPayment);
    }

    //No need to implement try catch and condition checks since this method will be
    //called by PurchaseServiceImpl
    @Transactional
    @Override
    public void updateSellerPayment(User seller, BigDecimal amountEarned, boolean isUpdateTimeOnly) {
        SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(seller.getId());
        if(sellerPayment == null)
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_PAYMENT_NOT_FOUND);
        Date now = new Date(System.currentTimeMillis());
        //If we are not updating time only, then we need to update amount earned
        if(!isUpdateTimeOnly) sellerPayment.setAmountEarned(amountEarned);
        sellerPayment.setModifiedAt(now);
        sellerPaymentRepository.save(sellerPayment);
    }

    @Transactional
    @Override
    public SellerReceiptResponse withdrawSellerPayment(Long sellerId)  {
        SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerId);
        if (sellerPayment == null)
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_PAYMENT_NOT_FOUND);
        User seller = userRepository.findById(sellerId).orElse(null);
        if (!LegitUtils.isSellerLegit(seller, true))
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_NOT_LEGIT);

        //double sellerWithdrawalClaim = Math.floor(sellerPayment.getAmountEarned() * (double) (100 - Constants.COMMISSION_PERCENTAGE) / 100);
        BigDecimal sellerWithdrawalClaim = sellerPayment.getAmountEarned()
                .multiply(BigDecimal.valueOf((double) (100 - Constants.COMMISSION_PERCENTAGE) / 100))
                .setScale(0, RoundingMode.FLOOR);

        BigDecimal sellerAmountPaid =
                sellerWithdrawalClaim.compareTo(BigDecimal.valueOf(Constants.MAXIMUM_WITHDRAWAL_AMOUNT)) > 0 ?
                BigDecimal.valueOf(Constants.MAXIMUM_WITHDRAWAL_AMOUNT) : sellerWithdrawalClaim.subtract(sellerPayment.getAmountPaid());

        Date now = new Date(System.currentTimeMillis());

        if (sellerAmountPaid.compareTo(BigDecimal.valueOf(Constants.MINIMUM_WITHDRAWAL_AMOUNT)) < 0) {
            Date lastPaidAt = sellerPayment.getLastPaidAt() != null ? sellerPayment.getLastPaidAt() : sellerPayment.getCreatedAt();
            if (!PaymentUtils.isSellerPaymentDue(now, lastPaidAt))
                throw new HotifiException(SellerPaymentErrorCodes.WITHDRAW_AMOUNT_PERIOD_ERROR);
            if(sellerAmountPaid.compareTo(BigDecimal.valueOf(Constants.MINIMUM_AMOUNT_INR)) < 0)
                throw new HotifiException(SellerPaymentErrorCodes.MINIMUM_WITHDRAWAL_AMOUNT_ERROR);
        }

        try {
            SellerReceiptResponse sellerReceiptResponse = sellerReceiptService.addSellerReceipt(seller, sellerPayment, sellerAmountPaid);
            //Following lines will continue after successful-1, processing-2, failure-3 payment
            SellerPaymentCodes sellerPaymentCodes = SellerPaymentCodes.fromInt(sellerReceiptResponse.getSellerReceipt().getStatus());
            Date lastPaidAt = sellerReceiptResponse.getSellerReceipt().getPaidAt();
            sellerPayment.setModifiedAt(now);
            switch (sellerPaymentCodes) {
                case PAYMENT_STARTED:
                case PAYMENT_SUCCESSFUL:
                    sellerPayment.setAmountPaid(sellerPayment.getAmountPaid().add(sellerAmountPaid));
                    sellerPayment.setLastPaidAt(lastPaidAt);
                    break;
                case PAYMENT_PROCESSING:
                    //TODO RazorPay's processing status
                    break;
                case PAYMENT_FAILED:
                    //TODO RazorPay's failure status
                    break;
            }
            sellerReceiptRepository.save(sellerReceiptResponse.getSellerReceipt());
            sellerPaymentRepository.save(sellerPayment);
            return sellerReceiptResponse;
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_PAYMENT_ERROR);
        }
    }

}
