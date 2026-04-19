package com.vittorhonorato.contac_sqs_dynamo.consumer;

import com.vittorhonorato.contac_sqs_dynamo.producer.EmailProducer;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EmailConsumer.class);

    @SqsListener("sqs-email")
    public void listen(String message) {
        logger.info("Consumindo mensagem ..... Mensagem: {}", message);
    }
}
