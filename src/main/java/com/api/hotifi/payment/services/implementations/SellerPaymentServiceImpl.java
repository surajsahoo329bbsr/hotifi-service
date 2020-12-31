package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.services.interfaces.ISellerPaymentService;
import com.api.hotifi.payment.services.interfaces.ISellerReceiptService;
import com.api.hotifi.payment.utils.PaymentUtils;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class SellerPaymentServiceImpl implements ISellerPaymentService {

    @Autowired
    private SellerPaymentRepository sellerPaymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ISellerReceiptService sellerReceiptService;

    //No need to implement try catch and condition checks since this method will be
    //called by PurchaseServiceImpl
    @Transactional
    @Override
    public void addSellerPayment(User seller, double amountEarned) {
        SellerPayment sellerPayment = new SellerPayment();
        sellerPayment.setSeller(seller);
        sellerPayment.setAmountEarned(amountEarned);
        sellerPaymentRepository.save(sellerPayment);
    }

    //No need to implement try catch and condition checks since this method will be
    //called by PurchaseServiceImpl
    @Transactional
    @Override
    public void updateSellerPayment(User seller, double amountEarned) {
        SellerPayment sellerPayment = sellerPaymentRepository.getOne(seller.getId());
        Date now = new Date(System.currentTimeMillis());
        double newAmountEarned = sellerPayment.getAmountEarned() + amountEarned;
        sellerPayment.setAmountEarned(newAmountEarned);
        sellerPayment.setModifiedAt(now);
        sellerPaymentRepository.save(sellerPayment);
    }

    @Transactional
    @Override
    public SellerReceiptResponse withdrawSellerPayment(Long sellerId) {
        try {
            SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerId);
            if (sellerPayment == null)
                throw new Exception("Seller Payment doesn't exist");
            User seller = userRepository.findById(sellerId).orElse(null);
            if (!LegitUtils.isSellerLegit(seller))
                throw new Exception("Seller is not legit to withdraw money");
            double sellerWithdrawalClaim = Math.floor(sellerPayment.getAmountEarned() * (double) (100 - Constants.COMMISSION_PERCENTAGE) / 100);
            double sellerAmountPaid = Double.compare(sellerWithdrawalClaim, Constants.MAXIMUM_WITHDRAWAL_AMOUNT) > 0 ? Constants.MAXIMUM_WITHDRAWAL_AMOUNT : sellerWithdrawalClaim;
            Date now = new Date(System.currentTimeMillis());

            if (Double.compare(sellerAmountPaid, Constants.MINIMUM_WITHDRAWAL_AMOUNT) < 0) {
                Date lastPaidAt = sellerPayment.getLastPaidAt() != null ? sellerPayment.getLastPaidAt() : sellerPayment.getCreatedAt();
                if (!PaymentUtils.isSellerPaymentDue(now, lastPaidAt))
                    throw new Exception("Please withdraw after " + (now.getTime() - lastPaidAt.getTime()) + " milliseconds");
                throw new Exception("Minimum withdrawal is " + Constants.MINIMUM_WITHDRAWAL_AMOUNT);
            }

            SellerReceiptResponse sellerReceiptResponse = sellerReceiptService.addSellerReceipt(seller, sellerPayment, sellerAmountPaid);
            //Following lines will continue after successful-1, processing-2, failure-3 payment
            int paymentStatus = sellerReceiptResponse.getSellerReceipt().getStatus();
            Date lastPaidAt = sellerReceiptResponse.getSellerReceipt().getPaidAt();
            sellerPayment.setModifiedAt(now);
            switch (paymentStatus) {
                case 1:
                    sellerPayment.setAmountEarned(sellerWithdrawalClaim - sellerAmountPaid);
                    sellerPayment.setAmountPaid(sellerPayment.getAmountPaid() + sellerAmountPaid);
                    sellerPayment.setLastPaidAt(lastPaidAt);
                    break;
                case 2:
                    //TODO RazorPay's processing status
                    break;
                case 3:
                    //TODO RazorPay's failure status
                    break;
            }
            sellerPaymentRepository.save(sellerPayment);
            return sellerReceiptResponse;
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

}
