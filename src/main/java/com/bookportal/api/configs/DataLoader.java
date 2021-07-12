package com.bookportal.api.configs;

import com.bookportal.api.entity.Role;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.enums.UserRoleEnum;
import com.bookportal.api.repository.RoleRepository;
import com.bookportal.api.repository.UserRepository;
import com.bookportal.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final Environment environment;

    @Override
    public void run(String... args) {
        String production = environment.getProperty("app.env");
        createRoles();
        if (!"prod".equals(production)) {
            createUser("admin@appmedia.com", UserRoleEnum.ROLE_ADMIN.name(), UserRoleEnum.ROLE_USER.name());
            createUser("user@appmedia.com", UserRoleEnum.ROLE_USER.name(), "");
            createUser("guest@appmedia.com", UserRoleEnum.ROLE_GUEST.name(), "");
            createSocialUser(UserRoleEnum.ROLE_USER.name());
        }
    }

    private void createUser(String mail, String role, String role2) {
        User user = new User();
        user.setMail(mail);
        user.setName("name");
        user.setSurname("surname");
        user.setPassword(new BCryptPasswordEncoder().encode("123"));
        user.setRoles(Arrays.asList(roleRepository.findByName(role), roleRepository.findByName(role2)));
        user.setActive(true);
        if (!userRepository.findByMail(mail).isPresent())
            userRepository.save(user);
    }

    private void createSocialUser(String role) {
        User user = new User();
        user.setMail("social@appmedia.com");
        user.setName("name");
        user.setSurname("surname");
        user.setPassword(new BCryptPasswordEncoder().encode("123"));
        user.setRoles(Arrays.asList(roleRepository.findByName(role), roleRepository.findByName("")));
        user.setActive(true);
        user.setSocial(true);
        user.setFacebookId("facebook");
        user.setGoogleId("google");
        if (!userRepository.findByMail("social@appmedia.com").isPresent())
            userRepository.save(user);
    }

    private void createRoles() {
        for (UserRoleEnum role : UserRoleEnum.values()) {
            Role myRole = new Role();
            myRole.setName(role.name());
            if (!roleRepository.existsByName(role.name()))
                roleRepository.save(myRole);
        }
    }

}