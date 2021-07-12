package com.bookportal.api.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@PropertySources({
        @PropertySource(value = "classpath:messages.yml", encoding = "UTF-8"),
        @PropertySource(value = "classpath:application.yml", encoding = "UTF-8")
})
public class EnvironmentVariables {
    private final Environment environment;

    public String eMailAlreadyInUse() {
        return environment.getProperty("mail-already-in-use");
    }

    public String tokenUserNotValid() {
        return environment.getProperty("token-mail-no-match");
    }

    public String incorrectCredentials() {
        return environment.getProperty("incorrect-credentials");
    }

    public String userInactive() {
        return environment.getProperty("inactive-user");
    }

    public String facebookIdAlreadyInUse() {
        return environment.getProperty("facebookId-already-in-use");
    }

    public String googleIdAlreadyInUse() {
        return environment.getProperty("googleId-already-in-use");
    }

    public String validityExpired() {
        return environment.getProperty("validity-expired");
    }

    public String userAlreadyConfirmedEmail() {
        return environment.getProperty("already-confirmed-email");
    }

}


