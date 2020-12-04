package com.api.hotifi.notification.entity;

import com.api.hotifi.identity.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Entity
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    //Id to go to certain page if clicked
    @Column(columnDefinition = "BIGINT")
    private long navId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    private String photoUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Timestamp(System.currentTimeMillis());

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isSeen = false;

}
