package com.yunus.sen.orderservice.event;

import com.yunus.sen.commonsservice.Event;
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

    private final KafkaTemplate<String, Event> kafkaTemplate;

    private final KafkaTopicProperties kafkaTopicProperties;

    @Override
    public void send(Event event) {
        ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(kafkaTopicProperties.getMail(), event.getEventId(), event);
        future.addCallback(EventFuture.getEventFuture());
    }


    static class EventFuture implements ListenableFutureCallback<SendResult<String, Event>> {
        private static EventFuture eventFuture;

        @Override
        public void onFailure(Throwable ex) {
            log.error("Error sending..." + ex);
        }

        @Override
        public void onSuccess(SendResult<String, Event> result) {
            log.info("Sended event to topic. Event: {} " + result.getProducerRecord().value());
        }

        private static EventFuture getEventFuture() {
            if (Objects.isNull(EventFuture.eventFuture)) {
                return new EventFuture();
            }
            return eventFuture;
        }
    }
}
