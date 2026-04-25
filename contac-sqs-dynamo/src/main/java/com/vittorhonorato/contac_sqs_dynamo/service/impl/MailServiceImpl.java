package com.vittorhonorato.contac_sqs_dynamo.service.impl;

import com.vittorhonorato.contac_sqs_dynamo.entity.ContactEntity;
import com.vittorhonorato.contac_sqs_dynamo.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.password}")
    private String to;

    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(ContactEntity contact) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Novo contato recebido: " + contact.getSubject());
        message.setText("""
                Nome: %s
                Email: %s

                Mensagem:
                %s
                """.formatted(
                contact.getName(),
                contact.getEmail(),
                contact.getMessage()
        ));

        javaMailSender.send(message);
    }
}
