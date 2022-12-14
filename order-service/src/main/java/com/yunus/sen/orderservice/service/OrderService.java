package com.yunus.sen.orderservice.service;

import com.yunus.sen.commonsservice.dto.Order;
import com.yunus.sen.commonsservice.dto.OrderEvent;
import com.yunus.sen.commonsservice.dto.OrderStatus;
import com.yunus.sen.orderservice.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final EventPublisher kafkaEventPublisher;

    public void createOrder(Order order) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            Thread.sleep(1000);
            kafkaEventPublisher.send(OrderEvent.builder()
                    .order(order)
                    .status(OrderStatus.IN_PROGRESS)
                    .counter(i)
                    .build());
        }
    }
}
