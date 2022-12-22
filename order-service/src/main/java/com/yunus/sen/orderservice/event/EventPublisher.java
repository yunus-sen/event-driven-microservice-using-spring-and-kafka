package com.yunus.sen.orderservice.event;

import com.yunus.sen.commonsservice.Event;
import com.yunus.sen.commonsservice.dto.OrderEvent;

public interface EventPublisher {
    void send(OrderEvent event);
}
