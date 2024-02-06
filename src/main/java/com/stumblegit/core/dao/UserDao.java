package com.stumblegit.core.dao;

import com.stumblegit.core.model.User;
import com.stumblegit.core.model.UserExample;
import org.apache.ibatis.annotations.Param;

public interface UserDao extends BaseDao<User, Integer, UserExample> {
    public User loadUserByEmail(String email);
    public Boolean userExists(String username);

    public void createUser(@Param("user") User user);
}

