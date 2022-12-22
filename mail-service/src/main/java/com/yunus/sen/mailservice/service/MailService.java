package com.yunus.sen.mailservice.service;

import com.yunus.sen.commonsservice.dto.OrderEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@Slf4j
public class MailService {

    public void send(OrderEvent event) {
        log.info("mail sended. counter : {} ", event.getCounter());
    }
}
