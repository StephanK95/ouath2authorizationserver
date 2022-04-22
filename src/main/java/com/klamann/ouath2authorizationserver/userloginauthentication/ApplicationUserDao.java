package com.klamann.ouath2authorizationserver.userloginauthentication;

import java.util.Optional;

public interface ApplicationUserDao {

    Optional<ApplicationUser> selectApplicationUserByUsername(String username);

}
