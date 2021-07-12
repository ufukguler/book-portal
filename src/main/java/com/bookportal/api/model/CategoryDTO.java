package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CategoryDTO {
    @Size(min = 3)
    @NotNull
    @NotBlank
    @ApiModelProperty(example = "category")
    private String category;
}
