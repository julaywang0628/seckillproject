package com.seckillproject.service;

import com.seckillproject.error.BizException;
import com.seckillproject.service.model.UserModel;

public interface UserService {

    //to get user object via id
    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BizException;


    /*
    telephone:registered telephone
    password:encoded password
     */
    UserModel validateLogin(String telephone, String encrptPassword) throws BizException;
}
