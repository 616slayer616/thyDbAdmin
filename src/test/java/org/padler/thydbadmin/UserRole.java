package org.padler.thydbadmin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "user_role")
public class UserRole implements Serializable {

    @Id
    @Column(name = "user_role_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userRoleId;

    @NaturalId
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "roles")
    @JsonIgnore
    private Collection<User> users = new HashSet<>();

    public UserRole(String name) {
        this.name = name;
    }

}
