package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.entities.SellerReceipt;
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
        //TODO Razor Pay payment implementation
        //after razor pay payment
        SellerReceiptResponse receiptResponse = new SellerReceiptResponse();
        SellerReceipt sellerReceipt = new SellerReceipt();
        sellerReceipt.setSellerPayment(sellerPayment);
        //status - successful-1, processing-2, failure-3
        sellerReceipt.setAmountPaid(sellerAmountPaid); //TODO set amount paid from razor pay
        sellerReceipt.setStatus(1); //TODO set payment status from razor pay
        sellerReceipt.setPaidAt(new Date(System.currentTimeMillis())); //TODO set payment date from razor pay
        sellerReceipt.setTransactionId("1234589"); //TODO set payment id from razor pay
        receiptResponse.setSellerReceipt(sellerReceipt);
        receiptResponse.setHotifiUpiId(Constants.HOTIFI_UPI_ID);
        receiptResponse.setSellerUpiId(seller.getUpiId());
        return receiptResponse;
    }

    @Transactional
    @Override
    public SellerReceiptResponse getSellerReceipt(Long id) {
        try {
            SellerReceipt sellerReceipt = sellerReceiptRepository.findById(id).orElse(null);
            if (sellerReceipt == null)
                throw new Exception("Seller Receipt not found");
            if (sellerReceipt.getStatus() == 2) {
                //which means payment is processing...
                sellerReceipt.setStatus(1); //TODO set status after reading from razor pay
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
        }
        return null;
    }

    @Transactional
    @Override
    public List<SellerReceiptResponse> getSortedSellerReceiptsByDateTime(Long sellerPaymentId, int page, int size, boolean isDescending) {
        try {
            SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerPaymentId);
            if (sellerPayment == null)
                throw new Exception("No seller payment exist");
            Pageable pageable = isDescending ?
                    PageRequest.of(page, size, Sort.by("created_at").descending()) :
                    PageRequest.of(page, size, Sort.by("created_at"));
            return getSellerReceipts(sellerPaymentId, pageable, sellerPayment);
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public List<SellerReceiptResponse> getSortedSellerReceiptsByAmountPaid(Long sellerPaymentId, int page, int size, boolean isDescending) {
        try {
            SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerPaymentId);
            if (sellerPayment == null)
                throw new Exception("No seller payment exist");
            Pageable pageable = isDescending ?
                    PageRequest.of(page, size, Sort.by("amount_paid").descending()) :
                    PageRequest.of(page, size, Sort.by("amount_paid"));
            return getSellerReceipts(sellerPaymentId, pageable, sellerPayment);
        } catch (Exception e) {
            log.error("Error occurred ", e);
        }
        return null;
    }

    //User defined functions
    private List<SellerReceiptResponse> getSellerReceipts(Long sellerPaymentId, Pageable pageable, SellerPayment sellerPayment) throws Exception{
        List<SellerReceipt> sellerReceipts = sellerReceiptRepository.findSellerReceipts(sellerPaymentId, pageable);
        SellerReceiptResponse receiptResponse = new SellerReceiptResponse();
        if (sellerReceipts == null)
            throw new Exception("Seller Receipt not found");
        List<SellerReceiptResponse> sellerReceiptResponses = new ArrayList<>();
        for (SellerReceipt sellerReceipt : sellerReceipts) {
            if (sellerReceipt.getStatus() == 2) {
                //which means payment is processing...
                sellerReceipt.setStatus(1); //TODO set status after reading from razor pay
                sellerReceiptRepository.save(sellerReceipt);
            }
            String sellerUpiId = sellerPayment.getSeller().getUpiId();
            receiptResponse.setSellerReceipt(sellerReceipt);
            receiptResponse.setSellerUpiId(sellerUpiId);
            receiptResponse.setHotifiUpiId(Constants.HOTIFI_UPI_ID);
            sellerReceiptResponses.add(receiptResponse);
        }
        return  sellerReceiptResponses;
    }
}
