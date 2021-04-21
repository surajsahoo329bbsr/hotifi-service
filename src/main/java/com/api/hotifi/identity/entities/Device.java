package com.api.hotifi.identity.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
public class Device implements Serializable {

    @JsonIgnore
    @ManyToMany(mappedBy = "userDevices")
    Set<User> users;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String androidId;

    @Column(nullable = false, length = 1024)
    private String name;

    @Column(nullable = false, length = 4096)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date tokenCreatedAt = new Timestamp(System.currentTimeMillis());

}
