package com.klamann.ouath2authorizationserver.userloginauthentication.model;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_seq")
    @SequenceGenerator(name = "user_role_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @ElementCollection
    @Column(name = "granted_authority")
    @CollectionTable(name = "user_role_granted_authorities", joinColumns = @JoinColumn(name = "owner_id"))
    private Set<String> grantedAuthorities = new LinkedHashSet<>();

    @Column(name = "name", nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void setGrantedAuthorities(Set<String> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}