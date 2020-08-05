package org.padler.thydbadmin;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "users") // user is a reserved keyword in postgreSQL
public class User implements Serializable {

    @Id
    @Column(name = "user_id", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    @Size(max = 254)
    @NotEmpty
    private String email;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "first_name", nullable = false)
    @Size(max = 35)
    @NotEmpty
    @NotNull
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Size(max = 35)
    @NotEmpty
    @NotNull
    private String lastName;

    @Column(name = "password", nullable = false)
    @Size(max = 60)
    @NotEmpty
    @org.springframework.data.annotation.Transient
    private String password;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "user_role_id", referencedColumnName = "user_role_id"))
    private Collection<UserRole> roles = new HashSet<>();

}
