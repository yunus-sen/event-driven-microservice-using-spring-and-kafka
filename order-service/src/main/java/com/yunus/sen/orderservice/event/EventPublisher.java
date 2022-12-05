package com.yunus.sen.orderservice.event;

import com.yunus.sen.commonsservice.Event;

public interface EventPublisher {
    void send(Event event);
}
