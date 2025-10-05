package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingUsers;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.entity.UserAdapter;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security UserDetailsService that loads users by email address.
 */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ForAccessingUsers forAccessingUsers;

    /**
     * Loads the user details by email (username) for Spring Security authentication.
     *
     * @param username the user's email address
     * @return UserDetails for the authenticated user
     * @throws UsernameNotFoundException when no user with the given email exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = forAccessingUsers.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Not found"));
        return new UserAdapter(user);
    }
}
