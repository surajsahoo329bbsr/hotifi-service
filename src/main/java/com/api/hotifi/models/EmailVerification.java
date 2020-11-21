package com.api.hotifi.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Entity
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String token;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date tokenCreatedAt = new Timestamp(System.currentTimeMillis());

    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean isVerified = false;
}
