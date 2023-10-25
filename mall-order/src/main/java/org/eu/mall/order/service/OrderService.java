package org.eu.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 18:59:18
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

