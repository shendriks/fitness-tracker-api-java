package dev.shendriks.fitnesstrackerapi.developer;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DeveloperDetailsServiceImpl implements UserDetailsService {
    private final DeveloperRepository repository;

    public DeveloperDetailsServiceImpl(DeveloperRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Developer developer = repository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return new DeveloperAdapter(developer);
    }
}
