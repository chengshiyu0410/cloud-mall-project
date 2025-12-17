package com.csy.cloud.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csy.cloud.order.enrity.Order;
import com.csy.cloud.order.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Provider;
import java.util.List;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {
    /**
     * 查询某个用户所有的订单，并且金额大于指定值
     */
    public List<Order> listOrdersByUserId(Long userId, Double amount){
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                .gt(Order::getAmount, amount);

        return this.list(queryWrapper);
    }
}
