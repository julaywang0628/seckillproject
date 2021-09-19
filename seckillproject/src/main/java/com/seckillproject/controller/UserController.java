package com.seckillproject.controller;

import com.seckillproject.controller.viewobject.UserVO;
import com.seckillproject.error.BizException;
import com.seckillproject.error.EnumBizError;
import com.seckillproject.response.CommonReturnType;
import com.seckillproject.service.UserService;
import com.seckillproject.service.model.UserModel;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:8090", "null"}, allowCredentials = "true", allowedHeaders = "*")

public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //user login interface
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telephone") String telephone,
                                  @RequestParam(name = "password") String password) throws BizException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //verify parameter input
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone) || StringUtils.isEmpty(password)) {
            throw new BizException(EnumBizError.PARAMETER_VALIDATION_ERROR);
        }

        //login service: to verify whether login is valid
        UserModel userModel = userService.validateLogin(telephone, this.EncodeByMd5(password));

        //join the session of success
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);
    }

    //user register interface
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telephone") String telephone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password) throws BizException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //verify tel.num
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telephone);
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BizException(EnumBizError.PARAMETER_VALIDATION_ERROR, "SMS verification code is false");
        }

        //process of user register
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("viaphone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);

    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;

    }

    //user get opt message interface
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone") String telephone) {
        //create otp verify nums according to rules
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //correspond verify nums with user telephones, binding telephone nums with optcode via httpsession
        httpServletRequest.getSession().setAttribute(telephone, otpCode);


        //send verify nums to user via message, omit
        System.out.println("telephone = " + telephone + " & otpCode =" + otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BizException {
        //reference service to get correspond id's user object and return to front end
        UserModel userModel = userService.getUserById(id);

        if (userModel == null) {
            throw new BizException(EnumBizError.USER_NOT_EXIST);
        }

        //transfer core model user objects to view object available for UI
        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }

}
