package com.seckillproject.service.impl;

import com.seckillproject.dao.UserDOMapper;
import com.seckillproject.dao.UserPasswordDOMapper;
import com.seckillproject.dataobject.UserDO;
import com.seckillproject.dataobject.UserPasswordDO;
import com.seckillproject.error.BizException;
import com.seckillproject.error.EnumBizError;
import com.seckillproject.service.UserService;
import com.seckillproject.service.model.UserModel;
import com.seckillproject.validator.ValidationResult;
import com.seckillproject.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        //reference userdomapper to get user dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);

        if (userDO == null) {
            return null; //indicata no user exits
        }
        //get encrptpassword via user id
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        return convertFromDataObject(userDO, userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BizException {
        if (userModel == null) {
            throw new BizException(EnumBizError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userModel.getName())
//                              || userModel.getGender() == null
//                              ||userModel.getAge() ==null
//                              || StringUtils.isEmpty(userModel.getTelephone())){
//            throw new BizException(EnumBizError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BizException(EnumBizError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        //implements of model -> dataobject
        UserDO userDO = convertFromModel(userModel);
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException ex) {
            throw new BizException(EnumBizError.PARAMETER_VALIDATION_ERROR, "the telephone num has existed");
        }
        userDOMapper.insertSelective(userDO);

        userModel.setId(userDO.getId());

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }

    @Override
    public UserModel validateLogin(String telephone, String encrptPassword) throws BizException {
        //via phone to get user information
        UserDO userDO = userDOMapper.selectByTelephone(telephone);
        if (userDO == null) {
            throw new BizException(EnumBizError.USER_LOGIN_ERROR);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);

        //compare encoded password with the input password
        if (!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
            throw new BizException(EnumBizError.USER_LOGIN_ERROR);
        }
        return userModel;
    }

    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);

        return userDO;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        if (userDO == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);

        if (userPasswordDO == null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }
}
