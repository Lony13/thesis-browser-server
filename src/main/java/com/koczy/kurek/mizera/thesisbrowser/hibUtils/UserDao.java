package com.koczy.kurek.mizera.thesisbrowser.hibUtils;


import com.koczy.kurek.mizera.thesisbrowser.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface UserDao extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
