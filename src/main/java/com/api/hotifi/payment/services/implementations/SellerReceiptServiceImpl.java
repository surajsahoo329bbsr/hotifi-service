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
import java.util.Date;
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
            SellerReceiptResponse receiptResponse = paymentProcessor.startSellerPayment(sellerAmountPaid, seller.getLinkedAccountId(), seller.getAuthentication().getEmail());

            SellerReceipt sellerReceipt = new SellerReceipt();
            sellerReceipt.setSellerPayment(sellerPayment);
            sellerReceipt.setAmountPaid(receiptResponse.getSellerReceipt().getAmountPaid());
            sellerReceipt.setStatus(receiptResponse.getSellerReceipt().getStatus());
            sellerReceipt.setCreatedAt(receiptResponse.getSellerReceipt().getCreatedAt());
            sellerReceipt.setPaymentId(receiptResponse.getSellerReceipt().getPaymentId());
            sellerReceipt.setBankAccountNumber(seller.getSellerBankAccount().getBankAccountNumber());
            sellerReceipt.setBankIfscCode(seller.getSellerBankAccount().getBankIfscCode());
            receiptResponse.setSellerReceipt(sellerReceipt);
            receiptResponse.setHotifiBankAccount(Constants.HOTIFI_BANK_ACCOUNT);
            receiptResponse.setSellerLinkedAccountId(seller.getLinkedAccountId());

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
                SellerReceiptResponse receiptResponse = paymentProcessor.getSellerPaymentStatus(sellerReceiptRepository, sellerReceipt.getPaymentId());
                sellerReceipt.setStatus(receiptResponse.getSellerReceipt().getStatus());
                sellerReceiptRepository.save(sellerReceipt);
            }
            SellerPayment sellerPayment = sellerReceipt.getSellerPayment();
            String linkedAccountId = sellerPayment.getSeller().getLinkedAccountId();
            SellerReceiptResponse receiptResponse = new SellerReceiptResponse();
            receiptResponse.setSellerReceipt(sellerReceipt);
            receiptResponse.setSellerLinkedAccountId(linkedAccountId);
            receiptResponse.setHotifiBankAccount(Constants.HOTIFI_BANK_ACCOUNT);
            return receiptResponse;
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_RECEIPT_ERROR);
        }
    }

    @Transactional
    @Override
    public List<SellerReceiptResponse> getSortedSellerReceiptsByDateTime(Long sellerId, int page, int size, boolean isDescending) {
        SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerId);
        if (sellerPayment == null)
            throw new HotifiException(SellerPaymentErrorCodes.NO_SELLER_PAYMENT_EXISTS);
        try {
            Pageable pageable = isDescending ?
                    PageRequest.of(page, size, Sort.by("created_at").descending()) :
                    PageRequest.of(page, size, Sort.by("created_at"));
            return getSellerReceipts(sellerPayment.getId(), pageable, sellerPayment);
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_RECEIPT_ERROR);
        }
    }

    @Transactional
    @Override
    public List<SellerReceiptResponse> getSortedSellerReceiptsByAmountPaid(Long sellerId, int page, int size, boolean isDescending) {
        try {
            SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerId);
            if (sellerPayment == null)
                throw new HotifiException(SellerPaymentErrorCodes.NO_SELLER_PAYMENT_EXISTS);
            Pageable pageable = isDescending ?
                    PageRequest.of(page, size, Sort.by("amount_paid").descending()) :
                    PageRequest.of(page, size, Sort.by("amount_paid"));
            return getSellerReceipts(sellerPayment.getId(), pageable, sellerPayment);
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
                if (sellerReceipt.getStatus() <= SellerPaymentCodes.PAYMENT_PROCESSING.value()) {
                    Date paidAt = new Date(System.currentTimeMillis());
                    SellerReceiptResponse receipt = paymentProcessor.getSellerPaymentStatus(sellerReceiptRepository, sellerReceipt.getPaymentId());
                    sellerReceipt.setStatus(receipt.getSellerReceipt().getStatus());
                    sellerReceipt.setPaidAt(paidAt);
                    sellerReceipt.setModifiedAt(paidAt);
                    sellerReceiptRepository.save(sellerReceipt);

                    //updating seller_payment entity after withdrawing money
                    sellerPayment.setAmountPaid(sellerReceipt.getAmountPaid() + sellerPayment.getAmountPaid());
                    sellerPayment.setLastPaidAt(sellerReceipt.getPaidAt());
                    sellerPayment.setModifiedAt(sellerReceipt.getPaidAt());
                    sellerPaymentRepository.save(sellerPayment);

                }
                String linkedAccountId = sellerPayment.getSeller().getLinkedAccountId();
                receiptResponse.setSellerReceipt(sellerReceipt);
                receiptResponse.setSellerLinkedAccountId(linkedAccountId);
                receiptResponse.setHotifiBankAccount(Constants.HOTIFI_BANK_ACCOUNT);
                sellerReceiptResponses.add(receiptResponse);
            }
            return  sellerReceiptResponses;
        } catch (Exception e){
            log.error("Error occurred", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_RECEIPT_ERROR);
        }
    }
}
