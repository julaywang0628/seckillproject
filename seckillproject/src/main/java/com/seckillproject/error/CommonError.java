package com.seckillproject.error;

import com.seckillproject.response.CommonReturnType;

public interface CommonError {
    public int getErrCode();

    public String getErrMsg();

    public CommonError setErrMsg(String errMsg);
}
