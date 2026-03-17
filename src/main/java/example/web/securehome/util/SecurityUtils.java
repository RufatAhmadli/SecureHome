package example.web.securehome.util;

import example.web.securehome.entity.User;
import example.web.securehome.exception.custom.UnauthorizedException;
import example.web.securehome.exception.custom.UserNotFoundException;
import example.web.securehome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void verifyCurrentUser(Long id) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(id)) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
    }


}
