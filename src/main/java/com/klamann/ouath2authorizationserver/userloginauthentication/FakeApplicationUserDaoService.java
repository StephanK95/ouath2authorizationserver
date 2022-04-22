package com.klamann.ouath2authorizationserver.userloginauthentication;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.klamann.ouath2authorizationserver.security.ApplicationUserRole.OAUTHUSER;
import static com.klamann.ouath2authorizationserver.security.ApplicationUserRole.OTHERUSER;

@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    private List<ApplicationUser> getApplicationUsers() {
        List<ApplicationUser> applicationUsers = Lists.newArrayList(
                new ApplicationUser(
                        "annasmith",
                        passwordEncoder.encode("password"),
                        OTHERUSER.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                ),
                new ApplicationUser(
                        "linda",
                        passwordEncoder.encode("password"),
                        OAUTHUSER.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                )
        );

        return applicationUsers;
    }

}