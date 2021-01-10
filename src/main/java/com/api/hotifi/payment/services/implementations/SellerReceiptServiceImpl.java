package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.entities.SellerReceipt;
import com.api.hotifi.payment.error.SellerPaymentErrorCodes;
import com.api.hotifi.payment.processor.PaymentProcessor;
import com.api.hotifi.payment.processor.codes.PaymentGatewayCodes;
import com.api.hotifi.payment.processor.codes.SellerPaymentCodes;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.repositories.SellerReceiptRepository;
import com.api.hotifi.payment.services.interfaces.ISellerReceiptService;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SellerReceiptServiceImpl implements ISellerReceiptService {

    @Autowired
    private SellerReceiptRepository sellerReceiptRepository;

    @Autowired
    private SellerPaymentRepository sellerPaymentRepository;

    //DO NOT ADD TO CONTROLLER
    //No need to implement try catch and condition checks since this method will be
    //called by SellerPaymentServiceImpl
    @Transactional
    @Override
    public SellerReceiptResponse addSellerReceipt(User seller, SellerPayment sellerPayment, double sellerAmountPaid) {
        try {
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);
            SellerReceiptResponse receiptResponse = paymentProcessor.startSellerPayment(sellerAmountPaid, seller.getUpiId(), seller.getAuthentication().getEmail());

            SellerReceipt sellerReceipt = new SellerReceipt();
            sellerReceipt.setSellerPayment(sellerPayment);
            sellerReceipt.setAmountPaid(receiptResponse.getSellerAmountPaid());
            sellerReceipt.setStatus(Constants.SELLER_PAYMENT_START_VALUE_CODE + receiptResponse.getSellerReceipt().getStatus());
            sellerReceipt.setPaidAt(receiptResponse.getSellerReceipt().getPaidAt());
            sellerReceipt.setTransactionId(receiptResponse.getSellerReceipt().getTransactionId());
            receiptResponse.setSellerReceipt(sellerReceipt);
            receiptResponse.setHotifiUpiId(Constants.HOTIFI_UPI_ID);
            receiptResponse.setSellerUpiId(seller.getUpiId());

            return receiptResponse;
        } catch (Exception e){
            log.error("Error occurred ", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_RECEIPT_ERROR);
        }
    }

    @Transactional
    @Override
    public SellerReceiptResponse getSellerReceipt(Long id) {
        SellerReceipt sellerReceipt = sellerReceiptRepository.findById(id).orElse(null);
        if (sellerReceipt == null)
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_RECEIPT_NOT_FOUND);
        try {
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);
            if (sellerReceipt.getStatus() == SellerPaymentCodes.PAYMENT_PROCESSING.value()) {
                SellerReceiptResponse receiptResponse = paymentProcessor.getSellerPaymentStatus(sellerReceipt.getTransactionId());
                sellerReceipt.setStatus(receiptResponse.getSellerReceipt().getStatus());
                sellerReceiptRepository.save(sellerReceipt);
            }
            SellerPayment sellerPayment = sellerReceipt.getSellerPayment();
            String sellerUpiId = sellerPayment.getSeller().getUpiId();
            SellerReceiptResponse receiptResponse = new SellerReceiptResponse();
            receiptResponse.setSellerReceipt(sellerReceipt);
            receiptResponse.setSellerUpiId(sellerUpiId);
            receiptResponse.setHotifiUpiId(Constants.HOTIFI_UPI_ID);
            return receiptResponse;
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_RECEIPT_ERROR);
        }
    }

    @Transactional
    @Override
    public List<SellerReceiptResponse> getSortedSellerReceiptsByDateTime(Long sellerPaymentId, int page, int size, boolean isDescending) {
        SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerPaymentId);
        if (sellerPayment == null)
            throw new HotifiException(SellerPaymentErrorCodes.NO_SELLER_PAYMENT_EXISTS);
        try {
            Pageable pageable = isDescending ?
                    PageRequest.of(page, size, Sort.by("created_at").descending()) :
                    PageRequest.of(page, size, Sort.by("created_at"));
            return getSellerReceipts(sellerPaymentId, pageable, sellerPayment);
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_RECEIPT_ERROR);
        }
    }

    @Transactional
    @Override
    public List<SellerReceiptResponse> getSortedSellerReceiptsByAmountPaid(Long sellerPaymentId, int page, int size, boolean isDescending) {
        try {
            SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerPaymentId);
            if (sellerPayment == null)
                throw new HotifiException(SellerPaymentErrorCodes.NO_SELLER_PAYMENT_EXISTS);
            Pageable pageable = isDescending ?
                    PageRequest.of(page, size, Sort.by("amount_paid").descending()) :
                    PageRequest.of(page, size, Sort.by("amount_paid"));
            return getSellerReceipts(sellerPaymentId, pageable, sellerPayment);
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_RECEIPT_ERROR);
        }
    }

    //User defined functions
    private List<SellerReceiptResponse> getSellerReceipts(Long sellerPaymentId, Pageable pageable, SellerPayment sellerPayment) {
        List<SellerReceipt> sellerReceipts = sellerReceiptRepository.findSellerReceipts(sellerPaymentId, pageable);
        SellerReceiptResponse receiptResponse = new SellerReceiptResponse();
        if (sellerReceipts == null)
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_RECEIPT_NOT_FOUND);
        try {
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);
            List<SellerReceiptResponse> sellerReceiptResponses = new ArrayList<>();
            for (SellerReceipt sellerReceipt : sellerReceipts) {
                if (sellerReceipt.getStatus() == SellerPaymentCodes.PAYMENT_PROCESSING.value()) {
                    SellerReceiptResponse receipt = paymentProcessor.getSellerPaymentStatus(sellerReceipt.getTransactionId());
                    sellerReceipt.setStatus(receipt.getSellerReceipt().getStatus());
                    sellerReceiptRepository.save(sellerReceipt);
                }
                String sellerUpiId = sellerPayment.getSeller().getUpiId();
                receiptResponse.setSellerReceipt(sellerReceipt);
                receiptResponse.setSellerUpiId(sellerUpiId);
                receiptResponse.setHotifiUpiId(Constants.HOTIFI_UPI_ID);
                sellerReceiptResponses.add(receiptResponse);
            }
            return  sellerReceiptResponses;
        } catch (Exception e){
            log.error("Error occurred", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_RECEIPT_ERROR);
        }
    }
}
