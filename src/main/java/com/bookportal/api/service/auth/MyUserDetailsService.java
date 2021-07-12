package com.bookportal.api.service.auth;

import com.bookportal.api.configs.MyUserDetails;
import com.bookportal.api.entity.User;
import com.bookportal.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByMail(userName);

        user.orElseThrow(() -> new UsernameNotFoundException(userName + " not found"));

        return user.map(MyUserDetails::new).get();
    }
}
