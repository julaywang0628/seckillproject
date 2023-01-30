package com.seckillproject.service;

import com.seckillproject.error.BizException;
import com.seckillproject.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BizException;
}
