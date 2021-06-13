package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.Purchase;
import com.api.hotifi.payment.entities.SellerPayment;
import com.api.hotifi.payment.entities.SellerReceipt;
import com.api.hotifi.payment.error.PurchaseErrorCodes;
import com.api.hotifi.payment.error.SellerPaymentErrorCodes;
import com.api.hotifi.payment.processor.PaymentProcessor;
import com.api.hotifi.payment.processor.codes.BuyerPaymentCodes;
import com.api.hotifi.payment.processor.codes.PaymentGatewayCodes;
import com.api.hotifi.payment.processor.codes.SellerPaymentCodes;
import com.api.hotifi.payment.repositories.PurchaseRepository;
import com.api.hotifi.payment.repositories.SellerPaymentRepository;
import com.api.hotifi.payment.repositories.SellerReceiptRepository;
import com.api.hotifi.payment.services.interfaces.IPaymentService;
import com.api.hotifi.payment.utils.PaymentUtils;
import com.api.hotifi.payment.web.responses.PendingMoneyResponse;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PaymentServiceImpl implements IPaymentService {

    private final SellerPaymentRepository sellerPaymentRepository;
    private final SellerReceiptRepository sellerReceiptRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;

    public PaymentServiceImpl(SellerPaymentRepository sellerPaymentRepository, SellerReceiptRepository sellerReceiptRepository, UserRepository userRepository, PurchaseRepository purchaseRepository) {
        this.sellerPaymentRepository = sellerPaymentRepository;
        this.sellerReceiptRepository = sellerReceiptRepository;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
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
        if (sellerPayment == null)
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_PAYMENT_NOT_FOUND);
        Date now = new Date(System.currentTimeMillis());
        //If we are not updating time only, then we need to update amount earned
        if (!isUpdateTimeOnly) sellerPayment.setAmountEarned(amountEarned);
        sellerPayment.setModifiedAt(now);
        sellerPaymentRepository.save(sellerPayment);
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

    /**
     * Seller Api Calls
     */

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

    @Transactional
    @Override
    public SellerReceiptResponse withdrawSellerPayment(Long sellerId) {
        User seller = userRepository.findById(sellerId).orElse(null);
        if (!LegitUtils.isSellerLegit(seller, true))
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_NOT_LEGIT);
        SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerId);
        if (sellerPayment == null)
            throw new HotifiException(SellerPaymentErrorCodes.SELLER_PAYMENT_NOT_FOUND);
        //double sellerWithdrawalClaim = Math.floor(sellerPayment.getAmountEarned() * (double) (100 - Constants.COMMISSION_PERCENTAGE) / 100);
        BigDecimal sellerWithdrawalClaim = sellerPayment.getAmountEarned()
                .multiply(BigDecimal.valueOf((double) (100 - BusinessConfigurations.COMMISSION_PERCENTAGE) / 100))
                .setScale(0, RoundingMode.FLOOR);

        BigDecimal sellerAmountPaid =
                sellerWithdrawalClaim.compareTo(BigDecimal.valueOf(BusinessConfigurations.MAXIMUM_WITHDRAWAL_AMOUNT)) > 0 ?
                        BigDecimal.valueOf(BusinessConfigurations.MAXIMUM_WITHDRAWAL_AMOUNT) : sellerWithdrawalClaim.subtract(sellerPayment.getAmountPaid());

        Date now = new Date(System.currentTimeMillis());

        if (sellerAmountPaid.compareTo(BigDecimal.valueOf(BusinessConfigurations.MINIMUM_WITHDRAWAL_AMOUNT)) < 0) {
            Date lastPaidAt = sellerPayment.getLastPaidAt() != null ? sellerPayment.getLastPaidAt() : sellerPayment.getCreatedAt();
            if (!PaymentUtils.isSellerPaymentDue(now, lastPaidAt))
                throw new HotifiException(SellerPaymentErrorCodes.WITHDRAW_AMOUNT_PERIOD_ERROR);
            if (sellerAmountPaid.compareTo(BigDecimal.valueOf(BusinessConfigurations.MINIMUM_AMOUNT_INR)) < 0)
                throw new HotifiException(SellerPaymentErrorCodes.MINIMUM_WITHDRAWAL_AMOUNT_ERROR);
        }

        try {
            SellerReceiptResponse sellerReceiptResponse = addSellerReceipt(seller, sellerPayment, sellerAmountPaid);
            //Following lines will continue after successful-1, processing-2, failure-3 payment
            Date lastPaidAt = sellerReceiptResponse.getSellerReceipt().getPaidAt();
            sellerPayment.setAmountPaid(sellerPayment.getAmountPaid().add(sellerAmountPaid));
            sellerPayment.setLastPaidAt(lastPaidAt);
            sellerPayment.setModifiedAt(now);
            sellerReceiptRepository.save(sellerReceiptResponse.getSellerReceipt());
            sellerPaymentRepository.save(sellerPayment);
            return sellerReceiptResponse;
        } catch (Exception e) {
            log.error("Error occurred ", e);
            throw new HotifiException(SellerPaymentErrorCodes.UNEXPECTED_SELLER_PAYMENT_ERROR);
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

    private PendingMoneyResponse getSellerPayment(Long sellerId) {
        BigDecimal sellerAmount = new BigDecimal("0.00");
        BigDecimal buyerRefund = new BigDecimal("0.00");
        User seller = userRepository.findById(sellerId).orElse(null);
        if (seller == null || seller.getBankAccount() == null)
            return new PendingMoneyResponse(false, sellerAmount, null,
                    false, buyerRefund, null);

        SellerPayment sellerPayment = sellerPaymentRepository.findSellerPaymentBySellerId(sellerId);
        if (sellerPayment == null) {
            return new PendingMoneyResponse(false, sellerAmount, null,
                    false, buyerRefund, null);
        }

        //double sellerWithdrawalClaim = Math.floor(sellerPayment.getAmountEarned() * (double) (100 - Constants.COMMISSION_PERCENTAGE) / 100);
        BigDecimal sellerWithdrawalClaim = sellerPayment.getAmountEarned()
                .multiply(BigDecimal.valueOf((double) (100 - BusinessConfigurations.COMMISSION_PERCENTAGE) / 100))
                .setScale(0, RoundingMode.FLOOR);

        BigDecimal sellerAmountPaid =
                sellerWithdrawalClaim.compareTo(BigDecimal.valueOf(BusinessConfigurations.MAXIMUM_WITHDRAWAL_AMOUNT)) > 0 ?
                        BigDecimal.valueOf(BusinessConfigurations.MAXIMUM_WITHDRAWAL_AMOUNT) : sellerWithdrawalClaim.subtract(sellerPayment.getAmountPaid());

        Date now = new Date(System.currentTimeMillis());

        if (sellerAmountPaid.compareTo(BigDecimal.valueOf(BusinessConfigurations.MINIMUM_WITHDRAWAL_AMOUNT)) < 0) {
            Date lastPaidAt = sellerPayment.getLastPaidAt() != null ? sellerPayment.getLastPaidAt() : sellerPayment.getCreatedAt();
            boolean isSellerPaymentDue = !PaymentUtils.isSellerPaymentDue(now, lastPaidAt);
            boolean isMinimumWithdrawalAmount = sellerAmountPaid.compareTo(BigDecimal.valueOf(BusinessConfigurations.MINIMUM_AMOUNT_INR)) < 0;
            if (isSellerPaymentDue || isMinimumWithdrawalAmount)
                return new PendingMoneyResponse(false, sellerAmountPaid, lastPaidAt,
                        false, buyerRefund, null);
        }

        return new PendingMoneyResponse(true, sellerAmountPaid, null,
                false, new BigDecimal("0.00"), null);
    }

    /**
     * Buyer Api Calls
     */

    @Transactional
    @Override
    public void withdrawBuyerRefunds(Long buyerId) {
        User buyer = userRepository.findById(buyerId).orElse(null);
        if (LegitUtils.isBuyerLegit(buyer)) {
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Date currentTime = new Date(System.currentTimeMillis());
            Supplier<Stream<Purchase>> purchaseStreamSupplier = () -> purchaseRepository.findPurchasesByBuyerId(buyerId, pageable).stream();
            BigDecimal totalRefundAmount = purchaseStreamSupplier.get()
                    .filter(purchase -> purchase.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.REFUND_PENDING.value() && !PaymentUtils.isBuyerRefundDue(currentTime, purchase.getPaymentDoneAt()))
                    .map(Purchase::getAmountRefund)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalRefundAmount.compareTo(BigDecimal.ZERO) == 0)
                throw new HotifiException(PurchaseErrorCodes.BUYER_PENDING_REFUNDS_NOT_FOUND);

            List<Purchase> purchases = purchaseStreamSupplier.get()
                    .filter(purchase -> purchase.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.REFUND_PENDING.value()
                            && !PaymentUtils.isBuyerRefundDue(currentTime, purchase.getPaymentDoneAt()))
                    .collect(Collectors.toList());

            purchases.forEach(purchase -> {
                RefundReceiptResponse buyerRefund = paymentProcessor.startBuyerRefund(purchaseRepository, purchase.getAmountRefund(), purchase.getPaymentId());
                String refundPaymentId = buyerRefund.getPurchase().getRefundPaymentId();
                Date refundStartedAt = buyerRefund.getPurchase().getRefundStartedAt();
                String refundTransactionId = buyerRefund.getPurchase().getRefundTransactionId();
                int buyerPaymentStatus = buyerRefund.getPurchase().getStatus();
                purchaseRepository.updatePurchaseRefundStatus(buyerPaymentStatus, refundPaymentId, refundStartedAt, refundTransactionId, purchase.getId());
            });

        } else
            throw new HotifiException(PurchaseErrorCodes.PURCHASE_UPDATE_NOT_LEGIT);
    }

    @Transactional
    @Override
    public List<RefundReceiptResponse> getBuyerRefundReceipts(Long buyerId, int page, int size, boolean isDescending) {
        User buyer = userRepository.findById(buyerId).orElse(null);
        if (LegitUtils.isBuyerLegit(buyer)) {
            Pageable pageable = isDescending ? PageRequest.of(page, size, Sort.by("refund_done_at").descending()) :
                    PageRequest.of(page, size, Sort.by("refund_done_at"));
            List<Purchase> purchaseReceipts = purchaseRepository.findPurchasesByBuyerId(buyerId, pageable)
                    .stream()
                    .filter(purchase -> purchase.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE >= BuyerPaymentCodes.REFUND_PENDING.value())
                    .collect(Collectors.toList());
            List<RefundReceiptResponse> refundReceiptResponses = new ArrayList<>();
            PaymentProcessor paymentProcessor = new PaymentProcessor(PaymentGatewayCodes.RAZORPAY);
            purchaseReceipts.forEach(purchase -> {
                /*The below case will arise only on instant refunds or payments older than 6 months
                if (purchase.getStatus() % Constants.PAYMENT_METHOD_START_VALUE_CODE == BuyerPaymentCodes.REFUND_FAILED.value()) {

                }*/
                RefundReceiptResponse receiptResponse = paymentProcessor.getBuyerRefundStatus(purchaseRepository, purchase.getPaymentId(), true);
                //Setting up values from payment processor
                int status = receiptResponse.getPurchase().getStatus();
                Date refundDoneAt = receiptResponse.getPurchase().getRefundDoneAt();
                String refundPaymentId = receiptResponse.getPurchase().getRefundPaymentId();
                String refundTransactionId = receiptResponse.getPurchase().getRefundTransactionId();
                purchaseRepository.updatePurchaseRefundStatus(status, refundPaymentId, refundDoneAt, refundTransactionId, purchase.getId());

                purchase.setStatus(status);
                purchase.setRefundPaymentId(refundPaymentId);
                purchase.setRefundDoneAt(refundDoneAt);
                purchase.setRefundTransactionId(refundTransactionId);
                RefundReceiptResponse refundReceiptResponse = new RefundReceiptResponse(purchase, BusinessConfigurations.HOTIFI_BANK_ACCOUNT);
                refundReceiptResponses.add(refundReceiptResponse);
            });
            return refundReceiptResponses;
        }

        throw new HotifiException(PurchaseErrorCodes.BUYER_NOT_LEGIT);
    }

    @Transactional
    @Override
    public PendingMoneyResponse getPendingPayments(Long sellerId) {
        PendingMoneyResponse pendingSellerPayments
                = getSellerPayment(sellerId);
        return getBuyerRefund(sellerId, pendingSellerPayments);
    }

    private PendingMoneyResponse getBuyerRefund(Long buyerId, PendingMoneyResponse pendingMoneyResponse) {
        BigDecimal pendingBuyerRefund = new BigDecimal("0.00");
        Date oldestPurchaseDate = new Date(System.currentTimeMillis());
        User buyer = userRepository.findById(buyerId).orElse(null);
        if (LegitUtils.isBuyerLegit(buyer)) {
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Date currentTime = new Date(System.currentTimeMillis());
            Supplier<Stream<Purchase>> purchaseStreamSupplier = () -> purchaseRepository.findPurchasesByBuyerId(buyerId, pageable).stream();
            BigDecimal totalRefundAmount = purchaseStreamSupplier.get()
                    .filter(purchase -> purchase.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.REFUND_PENDING.value() && !PaymentUtils.isBuyerRefundDue(currentTime, purchase.getPaymentDoneAt()))
                    .map(Purchase::getAmountRefund)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalRefundAmount.compareTo(BigDecimal.ZERO) == 0) {
                pendingMoneyResponse.setBuyerRefundDue(false);
                pendingMoneyResponse.setBuyerRefund(pendingBuyerRefund);
                pendingMoneyResponse.setBuyerOldestPendingRefundAt(null);
                return pendingMoneyResponse;
            }

            List<Purchase> purchases = purchaseStreamSupplier.get()
                    .filter(purchase -> purchase.getStatus() % BusinessConfigurations.PAYMENT_METHOD_START_VALUE_CODE < BuyerPaymentCodes.REFUND_PENDING.value()
                            && !PaymentUtils.isBuyerRefundDue(currentTime, purchase.getPaymentDoneAt()))
                    .collect(Collectors.toList());

            if (purchases.size() == 0) {
                pendingMoneyResponse.setBuyerRefundDue(false);
                pendingMoneyResponse.setBuyerRefund(pendingBuyerRefund);
                pendingMoneyResponse.setBuyerOldestPendingRefundAt(null);
                return pendingMoneyResponse;
            }

            for (Purchase purchase : purchases) {
                pendingBuyerRefund = pendingBuyerRefund.add(purchase.getAmountRefund());
                if (purchase.getPaymentDoneAt().before(oldestPurchaseDate)) {
                    oldestPurchaseDate = purchase.getPaymentDoneAt();
                }
            }

            pendingMoneyResponse.setBuyerRefundDue(true);
            pendingMoneyResponse.setBuyerRefund(pendingBuyerRefund);
            pendingMoneyResponse.setBuyerOldestPendingRefundAt(oldestPurchaseDate);
            return pendingMoneyResponse;
        }
        pendingMoneyResponse.setBuyerRefundDue(false);
        pendingMoneyResponse.setBuyerRefund(pendingBuyerRefund);
        pendingMoneyResponse.setBuyerOldestPendingRefundAt(null);
        return pendingMoneyResponse;
    }


}
