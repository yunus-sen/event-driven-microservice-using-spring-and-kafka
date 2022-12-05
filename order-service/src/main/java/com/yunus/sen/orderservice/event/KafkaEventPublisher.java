package com.yunus.sen.orderservice.event;

import com.yunus.sen.commonsservice.dto.OrderEvent;
import com.yunus.sen.orderservice.configuration.KafkaProperties;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<UUID, OrderEvent> kafkaTemplate;

    private final KafkaProperties kafkaProperties;

    @Override
    public void send(OrderEvent event) {
        ListenableFuture<SendResult<UUID, OrderEvent>> future = kafkaTemplate.send(kafkaProperties.getMail(), UUID.randomUUID(), event);
        future.addCallback(EventFuture.getEventFuture());
    }

    private static class EventFuture implements ListenableFutureCallback<SendResult<UUID, OrderEvent>> {

        private static EventFuture eventFuture;

        @Override
        public void onFailure(Throwable ex) {
            log.error("Error sending..." + ex);
        }

        @Override
        public void onSuccess(SendResult<UUID, OrderEvent> result) {
            log.info("sended event. " + result.getProducerRecord().value());
        }

        public static EventFuture getEventFuture() {
            if (Objects.isNull(EventFuture.eventFuture)) {
                return new EventFuture();
            }
            return eventFuture;
        }
    }
}
