package com.csy.cloud.order.controller;

import com.csy.cloud.order.enrity.Order;
import com.csy.cloud.order.service.OrderService;
import com.csy.cloud.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/user/{userId}")
    public Result<List<Order>> getOrders(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") Double minAmount) {
        List<Order> orders = orderService.listOrdersByUserId(userId, minAmount);
        return Result.success(orders);
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return Result.success(order);
    }
}
