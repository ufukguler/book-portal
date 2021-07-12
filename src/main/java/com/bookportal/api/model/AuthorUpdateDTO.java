package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthorUpdateDTO {
    @NotBlank
    @ApiModelProperty(example = "name surname")
    private String name;

    @NotBlank
    @ApiModelProperty(example = "about")
    private String about;

    @ApiModelProperty(example = "image Url")
    private String imageUrl;
}
