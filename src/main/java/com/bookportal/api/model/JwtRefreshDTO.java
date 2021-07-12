package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class JwtRefreshDTO {
    @NotBlank
    @ApiModelProperty(example = "user@appmedia.com")
    private String mail;

    @NotBlank
    @Size(min = 10, max = 200)
    @ApiModelProperty(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGtpcm1pemliYWxvbi5jb20iLCJpYXQiOjE2MjQzOTc2MzIsImV4cCI6MTYyNTAwMjQzMn0.4Zo62ATPeAkB1TcAkMbUG6g3eWNH7igtinCEYf5fbWs")
    private String token;
}
