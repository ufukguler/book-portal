package com.bookportal.api.controllers;

import com.bookportal.api.configs.AmazonClient;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class BucketController {
    private final AmazonClient amazonClient;

    @PostMapping("/uploadFile")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Upload to aws s3")
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return this.amazonClient.uploadFile(file);
    }


    @DeleteMapping("/deleteFile")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Delete from aws s3")
    public String deleteFile(
            @ApiParam(defaultValue = "https://appmedia.s3.eu-south-1.amazonaws.com/1623831948900-c3uhsgo1vx541.jpg")
            @RequestParam(value = "url") String fileUrl) {
        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    }

}
