package com.bookportal.api.controllers;

import com.bookportal.api.entity.User;
import com.bookportal.api.model.UserRegisterDTO;
import com.bookportal.api.service.RegisterService;
import com.bookportal.api.util.mapper.UserMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;

    @PostMapping
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Register ~ sends confirmation e-mail")
    public ResponseEntity<?> createUser(
            @ApiParam(required = true) @Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        User user = UserMapper.userRegisterDTOtoUser(userRegisterDTO);
        return new ResponseEntity<>(registerService.createUser(user), HttpStatus.CREATED);
    }
}