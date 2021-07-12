package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BookUpdateDTO {
    @NotBlank
    @ApiModelProperty(example = "title")
    private String title;

    @NotNull
    @ApiModelProperty(example = "[1,2]")
    private Long[] authorIds;

    @NotNull
    @ApiModelProperty(example = "100")
    private int page;

    @NotNull
    @ApiModelProperty(example = "3")
    private Long publisherId;

    @NotNull
    @ApiModelProperty(example = "2010")
    private int year;

    @ApiModelProperty(example = "image url")
    private String imageUrl;

    @ApiModelProperty(example = "tag")
    private String tag;

    @NotNull
    @ApiModelProperty(example = "true")
    private Boolean isPublished;
}
