package ru.netology.moneytransfer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.moneytransfer.dto.CardToCardOperationDTO;
import ru.netology.moneytransfer.dto.ErrorDTO;
import ru.netology.moneytransfer.dto.OperationDTO;
import ru.netology.moneytransfer.model.Amount;
import ru.netology.moneytransfer.service.TransferService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegratedTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    private static final GenericContainer<?> moneyTransferService = new GenericContainer<>("money-transfer-service:latest:latest").withExposedPorts(5500);

    private static final CardToCardOperationDTO validOperation = new CardToCardOperationDTO(
            "4960144072893312",
            "11/23",
            "157",
            "4960149153260042",
            new Amount(12920, "RUR"));


    @Test
    void transferController_return200() {
        ResponseEntity<OperationDTO> response = restTemplate.postForEntity(
                "http://localhost:" + moneyTransferService.getMappedPort(5500) + "/transfer",
                validOperation,
                OperationDTO.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        System.out.println(response.getBody());
    }

}
