package com.yunus.sen.mailservice.event.handler;

import com.yunus.sen.commonsservice.Event;
import com.yunus.sen.commonsservice.dto.OrderEvent;
import com.yunus.sen.mailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import static com.yunus.sen.mailservice.configuration.KafkaConsumerConfig.KAFKA_LISTENER_FACTORY_CONTAINER;

@Slf4j
@Component("mailSenderEventHandler")
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class MailSenderEventHandler {
    private final MailService mailService;


    @RetryableTopic(
            attempts = "3",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            exclude = {SerializationException.class, DeserializationException.class}

    )
    @KafkaListener(
            topics = "${kafka.topics.mail}",
            containerFactory = KAFKA_LISTENER_FACTORY_CONTAINER
    )
    public void handleSendMessageEvent(OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {


        if(orderEvent.getCounter()==5)
            throw new RuntimeException();
        //mailService.send(orderEvent);

        log.info("consumner: {}", orderEvent.getCounter());
    }

    @DltHandler
    public void handleDlt(OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("DLT consumer: {} handled by dlq topic: {}", orderEvent.getCounter(), topic);
    }
}
