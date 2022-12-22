package com.yunus.sen.mailservice.event.handler.limit;

import com.yunus.sen.commonsservice.Event;
import com.yunus.sen.commonsservice.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
public class RateLimitInterceptor implements RecordInterceptor<String, OrderEvent> {

    @Value("${kafka.record-limit}")
    private Integer recordLimit;
    private Semaphore semaphore;

    @PostConstruct
    public void init() {
        initSemaphore();
    }

    private void initSemaphore() {
        semaphore = new Semaphore(
                recordLimit,
                Boolean.TRUE
        );
    }

    @Scheduled(cron = "${kafka.record-limit-reset-period}")
    public void resetPermits() {
        int releasablePermits = recordLimit - semaphore.availablePermits();

        //log.info("semafore release");
        semaphore.release(releasablePermits);
    }

    @Override
    public ConsumerRecord<String, OrderEvent> intercept(ConsumerRecord<String, OrderEvent> record) {
        try {

            log.info("intercep counter : {}", record.value().getCounter());
            semaphore.acquire();

        } catch (Exception e) {
            log.error("Error occured while acquiring semaphore.", e);
        }
        return record;
    }
}
