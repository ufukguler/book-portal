package com.bookportal.api.service.auth;

import com.bookportal.api.entity.User;
import com.bookportal.api.exception.MyUserNotFoundException;
import com.bookportal.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByMail(mail);
        if (!optionalUser.isPresent()) {
            throw new MyUserNotFoundException("User not found with this email: " + "'" + mail + "'");
        }
        User user = optionalUser.get();
        List grantList = new ArrayList();
        user.getRoles().forEach(role -> grantList.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(
                user.getMail(),
                user.getPassword(),
                grantList
        );
    }

}
