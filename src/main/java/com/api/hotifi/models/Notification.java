package com.api.hotifi.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private long userId;

    //Id to go to certain page if clicked
    @Column(columnDefinition = "BIGINT")
    private long navId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    private String photoUrl;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean isSeen = false;

}
