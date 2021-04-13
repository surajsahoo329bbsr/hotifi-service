package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SellerReceiptServiceImpl implements ISellerReceiptService {

    private final SellerReceiptRepository sellerReceiptRepository;
    private final SellerPaymentRepository sellerPaymentRepository;

    public SellerReceiptServiceImpl(SellerReceiptRepository sellerReceiptRepository, SellerPaymentRepository sellerPaymentRepository) {
        this.sellerReceiptRepository = sellerReceiptRepository;
        this.sellerPaymentRepository = sellerPaymentRepository;
    }

    //DO NOT ADD TO CONTROLLER
    //No need to implement try catch and condition checks since this method will be
    //called by SellerPaymentServiceImpl
    @Transactional
    @Override
    public SellerReceiptResponse addSellerReceipt(User seller, SellerPayment sellerPayment, BigDecimal sellerAmountPaid) {
        try {
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);
            String bankAccountNumber = seller.getBankAccount().getBankAccountNumber();
            String bankIfscCode = seller.getBankAccount().getBankIfscCode();
            String linkedAccountId = seller.getBankAccount().getLinkedAccountId();
            return paymentProcessor.startSellerPayment(sellerAmountPaid, linkedAccountId, bankAccountNumber, bankIfscCode);
        } catch (Exception e) {
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
            //if (sellerReceipt.getStatus() == SellerPaymentCodes.PAYMENT_PROCESSING.value()) {
            SellerReceiptResponse receiptResponse = paymentProcessor.getSellerPaymentStatus(sellerReceiptRepository, sellerReceipt.getTransferId());
            SellerReceipt latestSellerReceipt = receiptResponse.getSellerReceipt();
            sellerReceiptRepository.save(latestSellerReceipt);
            // }
            SellerPayment sellerPayment = sellerReceipt.getSellerPayment();
            String linkedAccountId = sellerPayment.getSeller().getBankAccount().getLinkedAccountId();
            receiptResponse.setSellerReceipt(sellerReceipt);
            receiptResponse.setSellerLinkedAccountId(linkedAccountId);
            receiptResponse.setHotifiBankAccount(BusinessConfigurations.HOTIFI_BANK_ACCOUNT);
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
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_PAYMENT_NOT_FOUND);
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
                throw new HotifiException(SellerPaymentErrorCodes.SELLER_PAYMENT_NOT_FOUND);
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
                if (sellerReceipt.getStatus() <= SellerPaymentCodes.PAYMENT_CREATED.value()) {
                    SellerReceiptResponse receipt = paymentProcessor.getSellerPaymentStatus(sellerReceiptRepository, sellerReceipt.getTransferId());
                    sellerReceipt = receipt.getSellerReceipt();
                    sellerReceiptRepository.save(sellerReceipt);

                    //updating seller_payment entity after withdrawing money
                    sellerPayment.setAmountPaid(sellerReceipt.getAmountPaid().add(sellerPayment.getAmountPaid()));
                    sellerPayment.setLastPaidAt(sellerReceipt.getPaidAt());
                    sellerPayment.setModifiedAt(sellerReceipt.getModifiedAt());
                    sellerPaymentRepository.save(sellerPayment);

                }
                String linkedAccountId = sellerPayment.getSeller().getBankAccount().getLinkedAccountId();
                receiptResponse.setSellerReceipt(sellerReceipt);
                receiptResponse.setSellerLinkedAccountId(linkedAccountId);
                receiptResponse.setHotifiBankAccount(BusinessConfigurations.HOTIFI_BANK_ACCOUNT);
                sellerReceiptResponses.add(receiptResponse);
            }
            return sellerReceiptResponses;
        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_RECEIPT_ERROR);
        }
    }
}
