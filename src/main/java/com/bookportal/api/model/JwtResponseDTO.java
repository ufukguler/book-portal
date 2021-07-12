package com.bookportal.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDTO {
    private int status;
    private String token;
    private String username;
    private String issuedAt;
    private String expireAt;
    private String name;
    private String surname;
    private String ppUrl;
    private Long userId;

    @Override
    public String toString() {
        return "JwtResponseDTO{" +
                "username='" + username + '\'' +
                ", issuedAt='" + issuedAt + '\'' +
                ", expireAt='" + expireAt + '\'' +
                ", userId=" + userId +
                '}';
    }
}
