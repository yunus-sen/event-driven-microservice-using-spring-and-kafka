package com.yunus.sen.orderservice.event;

import com.yunus.sen.commonsservice.Event;
import com.yunus.sen.commonsservice.dto.OrderEvent;
import com.yunus.sen.orderservice.configuration.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private final KafkaTopicProperties kafkaTopicProperties;

    @Override
    public void send(OrderEvent event) {
        ListenableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(kafkaTopicProperties.getMail(), event.getEventId(), event);
        future.addCallback(EventFuture.getEventFuture());
    }


    static class EventFuture implements ListenableFutureCallback<SendResult<String, OrderEvent>> {
        private static EventFuture eventFuture;

        @Override
        public void onFailure(Throwable ex) {
            log.error("Error sending..." + ex);
        }

        @Override
        public void onSuccess(SendResult<String, OrderEvent> result) {
            log.info("producer: {} " + result.getProducerRecord().value().getCounter());
        }

        private static EventFuture getEventFuture() {
            if (Objects.isNull(EventFuture.eventFuture)) {
                return new EventFuture();
            }
            return eventFuture;
        }
    }
}
