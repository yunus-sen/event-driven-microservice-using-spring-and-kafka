package com.yunus.sen.commonsservice.dto;

import com.yunus.sen.commonsservice.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent implements Event {
    private String owner;
    private OrderStatus status;
    private String message;
    private Order order;
    private int counter;

    @Override
    public Integer getCounter() {
        return counter;
    }
}
