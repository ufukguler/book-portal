package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UserDTO {
    @NotBlank
    @Email
    @ApiModelProperty(example = "user@appmedia.com")
    private String mail;

    @NotBlank
    @ApiModelProperty(example = "123")
    private String password;

}