package com.yunus.sen.orderservice.event;

import com.yunus.sen.commonsservice.dto.OrderEvent;

public interface EventPublisher {
    public void send( OrderEvent event);
}
