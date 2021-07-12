package com.bookportal.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class QuoteDTO {
    @NotBlank
    @ApiModelProperty(example = "quote")
    private String quote;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(example = "1")
    private Long bookId;
}
