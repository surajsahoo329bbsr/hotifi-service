package com.api.hotifi.payment.processor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Settlement {

    private String id;

    private String entity;

    private int amount;

    private String status;

    private int fees;

    private int tax;

    private String utr;

    private long createdAt;

}
