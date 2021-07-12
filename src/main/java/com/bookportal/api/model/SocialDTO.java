package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class SocialDTO {
    @NotBlank
    @Email
    @ApiModelProperty(example = "social@appmedia.com")
    private String mail;

    @ApiModelProperty(example = "name")
    private String name;

    @ApiModelProperty(example = "surname")
    private String surname;

    @NotBlank
    @ApiModelProperty(example = "0")
    private String socialType;

    @NotNull
    @ApiModelProperty(example = "123123")
    private String googleId;

    @NotNull
    @ApiModelProperty(example = "1231231")
    private String facebookId;

    @ApiModelProperty(example = "image url")
    private String ppUrl;
}
