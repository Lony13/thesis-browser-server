package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.User;
import com.koczy.kurek.mizera.thesisbrowser.model.UserDto;

import java.util.List;

public interface IUserService {

    User save(UserDto user);

    List<User> findAll();

    User findById(Integer id);
}
