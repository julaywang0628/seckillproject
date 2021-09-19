package com.seckillproject.controller;

import com.seckillproject.error.BizException;
import com.seckillproject.error.EnumBizError;
import com.seckillproject.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";

    //define exception handler

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex) {
        Map<String, Object> responseData = new HashMap<>();
        if (ex instanceof BizException) {
            BizException bizException = (BizException) ex;
            responseData.put("errCode", bizException.getErrCode());
            responseData.put("errMsg", bizException.getErrMsg());
        } else {
            responseData.put("errCode", EnumBizError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg", EnumBizError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData, "failure");
    }
}
