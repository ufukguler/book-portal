package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class BookDTO {
    @NotBlank
    @ApiModelProperty(example = "title")
    private String title;

    @NotNull
    @ApiModelProperty(example = "[1]")
    private Long[] authorIds;

    @NotNull
    @ApiModelProperty(example = "250")
    private int page;

    @NotNull
    @ApiModelProperty(example = "1")
    private Long publisherId;

    @NotNull
    @ApiModelProperty(example = "2000")
    private int year;

    @ApiModelProperty(example = "image url")
    private String imageUrl;

    @ApiModelProperty(example = "tag")
    private String tag;
}
