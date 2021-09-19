package com.seckillproject.error;

public class BizException extends Exception implements CommonError {

    private CommonError commonError;

    //receive enumBizError directly
    public BizException(CommonError commonError) {
        super();
        this.commonError = commonError;
    }

    public BizException(CommonError commonError, String errMsg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }


    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
