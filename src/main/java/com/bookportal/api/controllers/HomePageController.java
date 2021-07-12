package com.bookportal.api.controllers;

import com.bookportal.api.entity.HomePage;
import com.bookportal.api.model.enums.HomePageEnum;
import com.bookportal.api.service.BookService;
import com.bookportal.api.service.HomePageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomePageController {

    private final HomePageService homePageService;
    private final BookService bookService;

    @GetMapping
    @ApiOperation(value = "Homepage response", response = Page.class)
    public ResponseEntity<?> getHomeDate() {
        return ResponseEntity.ok(homePageService.homePageResponse());
    }

    @PostMapping("/save")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Homepage listing", response = Page.class)
    public ResponseEntity<?> saveHomeDate(
            @ApiParam(value = "", required = true, allowableValues = "0, 1, 2") @RequestParam(name = "type") String type,
            @ApiParam(value = "Image Url", required = true) @RequestParam(name = "imageUrl", required = false) String imageUrl,
            @ApiParam(value = "Desc.", required = true) @RequestParam(name = "description") String description,
            @ApiParam(required = true) @RequestParam(name = "bookId") Long bookId) {
        HomePage homePage = new HomePage();
        homePage.setBook(bookService.findByIdAndActiveTrueAndIsPublishedTrue(bookId));
        homePage.setType(HomePageEnum.findByType(type));
        homePage.setDescription(description);
        homePage.setImageUrl(imageUrl);
        return new ResponseEntity<>(homePageService.save(homePage), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Homepage listing", response = Boolean.class)
    public ResponseEntity<?> deleteHomeData(
            @ApiParam(value = "", required = true, allowableValues = "0, 1, 2")
            @RequestParam(name = "type") String type,
            @ApiParam(defaultValue = "1", required = true) @RequestParam(name = "bookId") Long bookId) {
        HomePage homePage = new HomePage();
        homePage.setBook(bookService.findByIdAndActiveTrueAndIsPublishedTrue(bookId));
        homePage.setType(HomePageEnum.findByType(type));
        return ResponseEntity.ok(homePageService.delete(homePage));
    }

}
