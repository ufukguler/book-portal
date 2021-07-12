package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserRegisterDTO {
    @NotBlank
    @Size(min = 7, max = 70)
    @Email
    @ApiModelProperty(example = "user@appmedia.com")
    private String mail;

    @NotBlank
    @Size(min = 6, max = 255)
    @ApiModelProperty(example = "123456")
    private String password;

    @NotBlank
    @Size(min = 2, max = 50)
    @ApiModelProperty(example = "ad")
    private String name;

    @NotBlank
    @Size(min = 2, max = 50)
    @ApiModelProperty(example = "soyad")
    private String surname;
}