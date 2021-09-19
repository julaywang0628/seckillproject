package com.seckillproject.error;

import com.seckillproject.response.CommonReturnType;

public enum EnumBizError implements CommonError {

    //common error
    PARAMETER_VALIDATION_ERROR(10001, "illegal parameter"),
    UNKNOWN_ERROR(10002, "unknown error"),

    //user message error
    USER_NOT_EXIST(20001, "no user exists"),
    USER_LOGIN_ERROR(20002, "telephone num or password is incorrect"),
    ;

    private EnumBizError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    private int errCode;
    private String errMsg;

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
