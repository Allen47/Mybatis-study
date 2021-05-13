package com.msq.dao;

import com.msq.pojo.User;

import java.util.List;

/**
 * @author Msq
 * @date 2021/5/13 - 21:42
 */
public interface UserMapper {
    List<User> getUserList();

    User getUserById(int id);
}
