package com.seckillproject.service;

import com.seckillproject.error.BizException;
import com.seckillproject.service.model.ItemModel;

import java.util.List;

public interface ItemService {
    ItemModel createItem(ItemModel itemModel) throws BizException;

    List<ItemModel> listItem();

    ItemModel getItemById(Integer id);

    boolean decreaseStock(Integer itemId, Integer amount) throws BizException;
}
