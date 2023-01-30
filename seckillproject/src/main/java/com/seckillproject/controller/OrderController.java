package com.seckillproject.controller;

import com.seckillproject.dao.OrderDOMapper;
import com.seckillproject.error.BizException;
import com.seckillproject.error.EnumBizError;
import com.seckillproject.response.CommonReturnType;
import com.seckillproject.service.OrderService;
import com.seckillproject.service.model.OrderModel;
import com.seckillproject.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("/order")
@RequestMapping("/order")
@CrossOrigin(origins = {"http://localhost:8090", "null"}, allowCredentials = "true", allowedHeaders = "*")

public class OrderController extends BaseController {
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private OrderService orderService;


    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount) throws BizException {

        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null || !isLogin.booleanValue()) {
            throw new BizException(EnumBizError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, amount);

        return CommonReturnType.create(null);
    }

}
