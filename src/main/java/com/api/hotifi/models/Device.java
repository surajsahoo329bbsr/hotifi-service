package com.api.hotifi.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Device {

    @ManyToMany(mappedBy = "userSet")
    Set<User> userSet = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String token;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private Date tokenCreatedAt = new Timestamp(System.currentTimeMillis());

}
