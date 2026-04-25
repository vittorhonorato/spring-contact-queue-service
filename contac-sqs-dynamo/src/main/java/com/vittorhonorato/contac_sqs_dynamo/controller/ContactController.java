package com.vittorhonorato.contac_sqs_dynamo.controller;

import com.vittorhonorato.contac_sqs_dynamo.controller.dto.request.ContactRequestDTO;
import com.vittorhonorato.contac_sqs_dynamo.controller.dto.response.ContactDetailsResponseDTO;
import com.vittorhonorato.contac_sqs_dynamo.controller.dto.response.ContactResponseDTO;
import com.vittorhonorato.contac_sqs_dynamo.service.ContactService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sender-email")
@CrossOrigin("http://localhost:4200")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("send")
    public ResponseEntity<ContactResponseDTO> createContact(@Valid @RequestBody ContactRequestDTO request) {

        logger.info("chamando api de criação de contato - body: {}", request);
        var response = contactService.create(request);

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContactDetailsResponseDTO>> findAll() {
        List<ContactDetailsResponseDTO> response = contactService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDetailsResponseDTO> findContractById(@PathVariable String id) {

        ContactDetailsResponseDTO response = contactService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("email")
    public ResponseEntity<ContactDetailsResponseDTO> findContractByEmail(@RequestParam String email) {

        ContactDetailsResponseDTO response = contactService.findByEmail(email);
        return ResponseEntity.ok(response);
    }
}
