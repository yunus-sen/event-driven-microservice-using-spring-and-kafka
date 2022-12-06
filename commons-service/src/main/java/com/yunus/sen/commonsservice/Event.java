package com.yunus.sen.commonsservice;

import java.util.UUID;

public interface Event {

    default String getEventId() {
        return UUID.randomUUID().toString();
    }

    Integer getCounter();
}
