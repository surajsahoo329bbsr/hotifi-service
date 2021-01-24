package com.api.hotifi.identity.entities;

import com.api.hotifi.identity.models.RoleName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "roles")
    private List<Authentication> authentications;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(System.currentTimeMillis());

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

}