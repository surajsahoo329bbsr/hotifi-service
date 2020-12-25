package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.payment.entity.SellerPayment;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.services.interfaces.ISellerPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class SellerPaymentServiceImpl implements ISellerPaymentService {

    @Autowired
    private SellerPaymentRepository sellerPaymentRepository;

    //No need to implement try catch and condition checks since this method will be
    //called by PurchaseSeviceImpl
    @Transactional
    @Override
    public void addSellerPayment(User seller, double amountEarned) {
        SellerPayment sellerPayment = new SellerPayment();
        sellerPayment.setSeller(seller);
        sellerPayment.setAmountEarned(amountEarned);
        sellerPaymentRepository.save(sellerPayment);
    }

    //No need to implement try catch and condition checks since this method will be
    //called by PurchaseSeviceImpl
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
}
