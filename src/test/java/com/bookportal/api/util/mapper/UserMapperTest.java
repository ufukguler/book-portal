package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.User;
import com.bookportal.api.model.SocialDTO;
import com.bookportal.api.model.UserRegisterDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void userRegisterDTOtoUser() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setMail("mail");
        dto.setPassword("1234");
        dto.setName("name");
        dto.setSurname("surname");

        User user = UserMapper.userRegisterDTOtoUser(dto);

        assertEquals(dto.getMail(), user.getMail());
        assertEquals(dto.getPassword(), user.getPassword());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getSurname(), user.getSurname());
    }

    @Test
    void socialToUser() {
        SocialDTO dto = initSocialUser();
        User user = UserMapper.socialToUser(dto);
        assertEquals(user.getMail(), dto.getMail());
        assertEquals(user.getSocialType(), dto.getSocialType());
        assertEquals(user.getGoogleId(), dto.getGoogleId());
        assertEquals(user.getFacebookId(), dto.getFacebookId());
        assertEquals(user.getPpUrl(), dto.getPpUrl());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getSurname(), dto.getSurname());
    }

    private SocialDTO initSocialUser() {
        SocialDTO socialRegisterDTO = new SocialDTO();
        socialRegisterDTO.setMail("e@mail.com");
        socialRegisterDTO.setName("name");
        socialRegisterDTO.setSurname("surname");
        socialRegisterDTO.setSocialType("1");
        socialRegisterDTO.setPpUrl("url");
        socialRegisterDTO.setGoogleId("");
        socialRegisterDTO.setFacebookId("");
        return socialRegisterDTO;
    }
}