package com.yunus.sen.orderservice.controller;

import com.yunus.sen.commonsservice.dto.Order;
import com.yunus.sen.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1")
public class OrderController {

    private final OrderService orderService;

    @PostMapping(path = "/orders")
    public ResponseEntity<?> create(@RequestBody Order order) {
        orderService.createOrder(order);
        return ResponseEntity.noContent().build();
    }

}
