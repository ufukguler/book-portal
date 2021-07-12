package com.bookportal.api.controllers;

import com.bookportal.api.entity.Publisher;
import com.bookportal.api.model.PublisherDTO;
import com.bookportal.api.model.PublisherUpdateDTO;
import com.bookportal.api.service.PublisherService;
import com.bookportal.api.util.mapper.PublisherMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/publisher")
@RequiredArgsConstructor
public class PublisherController {
    private final PublisherService publisherService;

    @GetMapping
    @ApiOperation(value = "list publishers")
    public Page<Publisher> getPublishersByPagination(
            @ApiParam(defaultValue = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(defaultValue = "5") @RequestParam(name = "size", defaultValue = "5") int size) {
        return publisherService.getAllByPagination(page, size);
    }

    @PostMapping
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Save publisher")
    public ResponseEntity<?> savePublisher(@Valid @RequestBody PublisherDTO dto) {
        return ResponseEntity.ok(publisherService.save(PublisherMapper.dtoToPublisher(dto)));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get publisher by ID")
    public ResponseEntity<?> findPublisherById(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(publisherService.findById(id));
    }

    @PutMapping("/{id}/update")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Update publisher")
    public ResponseEntity<?> updatePublisher(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id,
            @ApiParam(required = true) @Valid @RequestBody PublisherUpdateDTO dto) {
        Publisher byId = publisherService.findById(id);
        byId.setName(dto.getName());
        return ResponseEntity.ok(publisherService.update(byId));
    }

    @DeleteMapping("/{id}/delete")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Delete publisher")
    public ResponseEntity<?> deletePublisher(@PathVariable("id") Long id) {
        return ResponseEntity.ok(publisherService.delete(id));
    }
}
