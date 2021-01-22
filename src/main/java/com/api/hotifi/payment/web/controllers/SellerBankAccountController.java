package com.api.hotifi.payment.web.controllers;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.payment.entities.SellerBankAccount;
import com.api.hotifi.payment.services.interfaces.ISellerBankAccountService;
import com.api.hotifi.payment.web.request.SellerBankAccountRequest;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Api(tags = Constants.SELLER_BANK_ACCOUNT)
@RequestMapping(path = "/seller-bank-account")
public class SellerBankAccountController {

    @Autowired
    private ISellerBankAccountService sellerBankAccountService;

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSellerBankAccount(@RequestBody @Validated SellerBankAccountRequest sellerBankAccountRequest) {
        sellerBankAccountService.addSellerBankAccount(sellerBankAccountRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/update/seller", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSellerBankAccountBySeller(@RequestBody @Validated SellerBankAccountRequest sellerBankAccountRequest) {
        sellerBankAccountService.updateSellerBankAccountBySeller(sellerBankAccountRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/update/admin/success/{seller-id}/{linked-account-id}")
    public ResponseEntity<?> updateSuccessfulSellerBankAccountByAdmin(@PathVariable(value = "seller-id")
                                                                      @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                                      @PathVariable(value = "linked-account-id")
                                                                      @Length(max = 255, message = "{linked.account.id.length.invalid}") String linkedAccountId) {
        sellerBankAccountService.updateSellerBankAccountByAdmin(sellerId, linkedAccountId, null);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/update/admin/error/{seller-id}/{error-description}")
    public ResponseEntity<?> updateErrorSellerBankAccountByAdmin(@PathVariable(value = "seller-id")
                                                                 @Range(min = 1, message = "{seller.id.invalid}") Long sellerId,
                                                                 @PathVariable(value = "error-description", required = false)
                                                                 @Length(max = 255, message = "{error.description.length.invalid}") String errorDescription) {
        sellerBankAccountService.updateSellerBankAccountByAdmin(sellerId, null, errorDescription);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(path = "/get/seller/accounts/{seller-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSellerBankAccountBySellerId(@PathVariable(value = "seller-id") @Range(min = 1, message = "{seller.id.invalid}") Long sellerId) {
        SellerBankAccount sellerBankAccount = sellerBankAccountService.getSellerBankAccountBySellerId(sellerId);
        return new ResponseEntity<>(sellerBankAccount, HttpStatus.OK);
    }

    @GetMapping(path = "/get/admin/seller/accounts/unlinked", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sellerBankAccounts() {
        List<SellerBankAccount> sellerBankAccounts = sellerBankAccountService.getUnlinkedSellerBankAccounts();
        return new ResponseEntity<>(sellerBankAccounts, HttpStatus.OK);
    }

}
