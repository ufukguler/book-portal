package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PasswordResetDTO {
    @NotBlank
    @Email
    @ApiModelProperty(example = "user@appmedia.com")
    private String email;

    @NotBlank
    @ApiModelProperty(example = "confirmation token")
    private String key;

    @NotBlank
    @Size(min = 6, max = 255)
    @ApiModelProperty(example = "123456")
    private String newPass;
}
