package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.User;
import com.koczy.kurek.mizera.thesisbrowser.model.UserDto;

import java.util.List;

public interface IUserService {

    User save(UserDto user);

    List<User> findAll();

    void delete(long id);

    User findOne(String username);

    User findById(Long id);
}
