package emazon.cart.ports.persistence.mysql.adapter;

import emazon.cart.domain.spi.IAuthenticationPersistencePort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationAdapter implements IAuthenticationPersistencePort {
    @Override
    public Long getAuthenticatedUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf( userDetails.getUsername());
    }
}
