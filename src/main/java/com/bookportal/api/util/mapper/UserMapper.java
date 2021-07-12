package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.User;
import com.bookportal.api.model.SocialDTO;
import com.bookportal.api.model.UserRegisterDTO;

public class UserMapper {

    public static User socialToUser(SocialDTO socialRegisterDTO) {
        User user = new User();
        user.setMail(socialRegisterDTO.getMail());
        user.setName(socialRegisterDTO.getName());
        user.setSurname(socialRegisterDTO.getSurname());
        user.setSocialType(socialRegisterDTO.getSocialType());
        user.setPpUrl(socialRegisterDTO.getPpUrl());
        user.setGoogleId(socialRegisterDTO.getGoogleId());
        user.setFacebookId(socialRegisterDTO.getFacebookId());
        user.setSocial(true);
        return user;
    }

    public static User userRegisterDTOtoUser(UserRegisterDTO userRegisterDTO) {
        User user = new User();
        user.setMail(userRegisterDTO.getMail());
        user.setPassword(userRegisterDTO.getPassword());
        user.setName(userRegisterDTO.getName());
        user.setSurname(userRegisterDTO.getSurname());
        return user;
    }
}
