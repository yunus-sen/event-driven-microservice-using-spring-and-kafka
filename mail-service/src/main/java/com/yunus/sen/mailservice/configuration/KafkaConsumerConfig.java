package com.yunus.sen.mailservice.configuration;

import com.yunus.sen.commonsservice.Event;
import com.yunus.sen.commonsservice.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    public static final String KAFKA_LISTENER_FACTORY_CONTAINER = "kafkaListenerContainerFactory";


    @Value("${kafka.consumer.concurrency}")
    private Integer concurrency;

    private final KafkaProperties kafkaProperties;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEvent.class);


        return props;
    }

    @Bean
    public ConsumerFactory<String, OrderEvent> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(OrderEvent.class));
    }

    @Bean(name = KAFKA_LISTENER_FACTORY_CONTAINER)
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory(RecordInterceptor<String, OrderEvent> rateLimitingInterceptor) {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setRecordInterceptor(rateLimitingInterceptor);
        factory.setConcurrency(concurrency);
        /*factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template), new FixedBackOff(5000, 3))
        );*/
        return factory;
    }

   /* @Bean
    public DeadLetterPublishingRecoverer publisher(KafkaTemplate<String,OrderEvent > template) {


        return new DeadLetterPublishingRecoverer(template);
    }

    @Bean
    public RetryTopicConfiguration retryTopicConfig(KafkaTemplate<String, OrderEvent> template) {
        template.getProducerFactory().getConfigurationProperties();

        return RetryTopicConfigurationBuilder
                .newInstance()
                //.exponentialBackoff(2000, 5, Long.MAX_VALUE)
                //.maxAttempts(3)
                //.timeoutAfter(-1)
                //.autoCreateTopicsWith(3, (short) 3)
                //.dltProcessingFailureStrategy(DltStrategy.FAIL_ON_ERROR)
                //.setTopicSuffixingStrategy(TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
                .retryTopicSuffix("-retry")
                .dltSuffix("-dlt")
                .dltHandlerMethod("mailSenderEventHandler","handleDlt")
                .listenerFactory(KAFKA_LISTENER_FACTORY_CONTAINER)
                .create(template);
    }*/
}
