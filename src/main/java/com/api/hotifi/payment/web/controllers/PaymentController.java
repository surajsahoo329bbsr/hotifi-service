package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.authorization.service.ICustomerAuthorizationService;
import com.api.hotifi.authorization.utils.AuthorizationUtils;
import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.common.constants.messages.SuccessMessages;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.payment.model.PendingTransfer;
import com.api.hotifi.payment.model.UpiPendingTransfer;
import com.api.hotifi.payment.services.interfaces.IPaymentService;
import com.api.hotifi.payment.web.responses.PendingMoneyResponse;
import com.api.hotifi.payment.web.responses.RefundReceiptResponse;
import com.api.hotifi.payment.web.responses.SellerReceiptResponse;
import com.api.hotifi.session.model.TransferUpdate;
import com.api.hotifi.session.model.UpiTransferUpdate;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Api(tags = AppConfigurations.PAYMENT_TAG)
@RequestMapping(path = "/payment")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private ICustomerAuthorizationService customerAuthorizationService;

    @PutMapping(path = "/seller/claim/withdraw/{seller-id}")
    @ApiOperation(
            value = "Withdraw Claim Seller Earnings By Seller Id ",
            notes = "Withdraw Claim Seller Earnings By Seller Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 204, message = SuccessMessages.OK, response = SellerReceiptResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> notifySellerWithdrawalForAdmin(@PathVariable(value = "seller-id") @Range(min = 1, message = "{seller.id.invalid}") Long sellerId) {
        if (customerAuthorizationService.isAuthorizedByUserId(sellerId, AuthorizationUtils.getUserToken()))
            paymentService.notifySellerWithdrawalForAdmin(sellerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/admin/seller/pending/payments")
    @ApiOperation(
            value = "Get All Pending Seller Payments For Admin",
            notes = "Get All Pending Seller Payments For Admin",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = SellerReceiptResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getAllPendingSellerPaymentsForAdmin() {
        List<PendingTransfer> pendingTransfers = AuthorizationUtils.isAdministratorRole() ? paymentService.getAllPendingSellerPaymentsForAdmin() : null;
        return new ResponseEntity<>(pendingTransfers, HttpStatus.OK);
    }

    @GetMapping(path = "/admin/seller/pending/payments/upi")
    @ApiOperation(
            value = "Get All Pending UPI Seller Payments For Admin",
            notes = "Get All Pending UPI Seller Payments For Admin",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = SellerReceiptResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getAllPendingUpiSellerPaymentsForAdmin() {
        List<UpiPendingTransfer> upiPendingTransfers = AuthorizationUtils.isAdministratorRole() ? paymentService.getAllPendingUpiSellerPaymentsForAdmin() : null;
        return new ResponseEntity<>(upiPendingTransfers, HttpStatus.OK);
    }

    @PutMapping(path = "/admin/seller/pending/payments/update")
    @ApiOperation(
            value = "Update Seller Earnings By Seller Id By Admin",
            notes = "Update Seller Earnings By Seller Id By Admin",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 204, message = SuccessMessages.OK, response = SellerReceiptResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> updatePendingSellerPaymentsByAdmin(@RequestBody List<TransferUpdate> transfers) {
        if (AuthorizationUtils.isAdministratorRole())
            paymentService.updatePendingSellerPaymentsByAdmin(transfers);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/admin/seller/pending/payments/upi/update")
    @ApiOperation(
            value = "Update Seller Earnings By Seller Id By Admin After UPI Payment",
            notes = "Update Seller Earnings By Seller Id By Admin After UPI Payment",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 204, message = SuccessMessages.OK, response = SellerReceiptResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> updatePendingUpiSellerPaymentsByAdmin(@RequestBody List<UpiTransferUpdate> upiTransferUpdates) {
        if (AuthorizationUtils.isAdministratorRole())
            paymentService.updatePendingUpiSellerPaymentsByAdmin(upiTransferUpdates);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/seller/withdraw/{seller-id}")
    @ApiOperation(
            value = "Withdraw Seller Earnings By Seller Id",
            notes = "Withdraw Seller Earnings By Seller Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = SellerReceiptResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> withdrawSellerPayment(@PathVariable(value = "seller-id") @Range(min = 1, message = "{seller.id.invalid}") Long sellerId) {
        SellerReceiptResponse sellerReceiptResponse =
                customerAuthorizationService.isAuthorizedByUserId(sellerId, AuthorizationUtils.getUserToken()) ?
                        paymentService.withdrawSellerPayment(sellerId) : null;
        return new ResponseEntity<>(sellerReceiptResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/seller/receipt/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Seller Receipt By Seller Receipt Id",
            notes = "Get Seller Receipt By Seller Receipt Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = SellerReceiptResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSellerReceipt(@PathVariable(value = "id") @Range(min = 1, message = "{id.invalid}") Long id) {
        SellerReceiptResponse sellerReceiptResponse =
                AuthorizationUtils.isAdministratorRole() && customerAuthorizationService.isAuthorizedBySellerReceiptId(id, AuthorizationUtils.getUserToken()) ?
                        paymentService.getSellerReceipt(id) : null;
        return new ResponseEntity<>(sellerReceiptResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/seller/receipts/date-time/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Date-Time Seller Receipts By Seller Id",
            notes = "Get Sorted Date-Time Seller Receipts By Seller Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = SellerReceiptResponse.class, responseContainer = "List")
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSortedSellerReceiptsByDateTime(@PathVariable(value = "seller-id")
                                                               @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                               @PathVariable(value = "page")
                                                               @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                               @PathVariable(value = "size")
                                                               @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                               @PathVariable(value = "is-descending") boolean isDescending) {
        List<SellerReceiptResponse> sellerReceiptResponses =
                AuthorizationUtils.isAdministratorRole() || customerAuthorizationService.isAuthorizedByUserId(sellerId, AuthorizationUtils.getUserToken()) ?
                        paymentService.getSortedSellerReceiptsByDateTime(sellerId, page, size, isDescending) : null;
        return new ResponseEntity<>(sellerReceiptResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/seller/receipts/amount-paid/{seller-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Sorted Amount-Paid Seller Receipts By Seller Id",
            notes = "Get Sorted Amount-Paid Seller Receipts By Seller Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = SellerReceiptResponse.class, responseContainer = "List")
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getSortedSellerReceiptsByAmountPaid(@PathVariable(value = "seller-id")
                                                                 @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                                 @PathVariable(value = "page")
                                                                 @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
                                                                 @PathVariable(value = "size")
                                                                 @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
                                                                 @PathVariable(value = "is-descending") boolean isDescending) {
        List<SellerReceiptResponse> sellerReceiptResponses =
                AuthorizationUtils.isAdministratorRole() || customerAuthorizationService.isAuthorizedByUserId(sellerId, AuthorizationUtils.getUserToken()) ?
                        paymentService.getSortedSellerReceiptsByAmountPaid(sellerId, page, size, isDescending) : null;
        return new ResponseEntity<>(sellerReceiptResponses, HttpStatus.OK);
    }


    @PutMapping(path = "/buyer/refunds/{buyer-id}")
    @ApiOperation(
            value = "Withdraw Buyer Refunds By Buyer Id",
            notes = "Withdraw Buyer Refunds By Buyer Id",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> withdrawBuyerRefunds(@PathVariable(value = "buyer-id")
                                                  @Range(min = 1, message = "{buyer.id.invalid}") Long buyerId) {
        if (customerAuthorizationService.isAuthorizedByUserId(buyerId, AuthorizationUtils.getUserToken()))
            paymentService.withdrawBuyerRefunds(buyerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/buyer/refunds/receipts/{buyer-id}/{page}/{size}/{is-descending}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Buyer Refund Receipts By Buyer Id And Pagination Values",
            notes = "Get Buyer Refund Receipts By Buyer Id And Pagination Values",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = RefundReceiptResponse.class, responseContainer = "List")
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getBuyerRefundReceipts(
            @PathVariable(value = "buyer-id")
            @Range(min = 1, message = "{buyer.id.invalid}") Long buyerId,
            @PathVariable(value = "page")
            @Range(min = 0, max = Integer.MAX_VALUE, message = "{page.number.invalid}") int page,
            @PathVariable(value = "size")
            @Range(min = 1, max = Integer.MAX_VALUE, message = "{page.size.invalid}") int size,
            @PathVariable(value = "is-descending") boolean isDescending) {
        List<RefundReceiptResponse> refundReceiptResponses =
                customerAuthorizationService.isAuthorizedByUserId(buyerId, AuthorizationUtils.getUserToken()) ?
                        paymentService.getBuyerRefundReceipts(buyerId, page, size, isDescending) : null;
        return new ResponseEntity<>(refundReceiptResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/is-due/{user-id}")
    @ApiOperation(
            value = "Check If Any Payment Is Due By User Id",
            notes = "Check If Any Payment Is Due By User Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = PendingMoneyResponse.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> isPaymentDue(@PathVariable(value = "user-id") @Range(min = 1, message = "{user.id.invalid}") Long userId) {
        PendingMoneyResponse pendingMoneyResponse =
                customerAuthorizationService.isAuthorizedByUserId(userId, AuthorizationUtils.getUserToken()) ?
                        paymentService.getPendingPayments(userId) : null;
        return new ResponseEntity<>(pendingMoneyResponse, HttpStatus.OK);
    }

}
