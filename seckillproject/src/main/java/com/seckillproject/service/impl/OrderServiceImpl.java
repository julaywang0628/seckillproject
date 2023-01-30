package com.seckillproject.service.impl;

import com.seckillproject.dao.OrderDOMapper;
import com.seckillproject.dao.SequenceDOMapper;
import com.seckillproject.dataobject.OrderDO;
import com.seckillproject.dataobject.SequenceDO;
import com.seckillproject.error.BizException;
import com.seckillproject.error.EnumBizError;
import com.seckillproject.service.ItemService;
import com.seckillproject.service.OrderService;
import com.seckillproject.service.UserService;
import com.seckillproject.service.model.ItemModel;
import com.seckillproject.service.model.OrderModel;
import com.seckillproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BizException {
        //verify the status of ordering
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null) {
            throw new BizException(EnumBizError.PARAMETER_VALIDATION_ERROR, "The item info doesn't exist.");
        }

        UserModel userModel = userService.getUserById(userId);
        if(userModel == null) {
            throw new BizException(EnumBizError.PARAMETER_VALIDATION_ERROR, "The user info doesn't exist.");
        }
        if(amount <= 0 || amount > 99) {
            throw new BizException(EnumBizError.PARAMETER_VALIDATION_ERROR, "The amount is invalid.");
        }

        //reduce storage after ordering
        boolean result = itemService.decreaseStock(itemId, amount);
        if(!result) {
            throw new BizException(EnumBizError.STOCK_NOT_ENOUGH);
        }


        //pass the order data to the storage.
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));

        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //return to the front end
        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo() {
        StringBuilder stringBuilder = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for(int i = 0; i < 6 - sequenceStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        stringBuilder.append("00");
        return stringBuilder.toString();


    }

    private OrderDO convertFromOrderModel(OrderModel orderModel) {
        if(orderModel == null) {
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        return orderDO;
    }
}
