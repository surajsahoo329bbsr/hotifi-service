package com.api.hotifi.session.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class UpiTransferUpdate {

    private Long sellerId;

    private Date paidAt;

    private String utr;

    private String upiTransactionId;

    private int status;

    private String errorDescription;

}
