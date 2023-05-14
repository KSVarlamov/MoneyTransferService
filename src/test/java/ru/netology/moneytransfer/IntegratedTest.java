package ru.netology.moneytransfer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.moneytransfer.dto.CardToCardOperationDTO;
import ru.netology.moneytransfer.dto.ConfirmOperationDTO;
import ru.netology.moneytransfer.dto.ErrorDTO;
import ru.netology.moneytransfer.dto.OperationDTO;
import ru.netology.moneytransfer.model.Amount;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegratedTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    private static final GenericContainer<?> moneyTransferService = new GenericContainer<>("money-transfer-service:latest:latest").withExposedPorts(5500);

    private static final CardToCardOperationDTO validOperation = CardToCardOperationDTO.builder()
            .cardFromNumber("4960144072893312")
            .cardFromValidTill("11/23")
            .cardFromCVV("157")
            .cardToNumber("4960149153260042")
            .amount(new Amount(12920, "RUR"))
            .build();

    private static final CardToCardOperationDTO invalidCardInOperation = CardToCardOperationDTO.builder()
            .cardFromNumber("zzz")
            .cardFromValidTill("11/23")
            .cardFromCVV("157")
            .cardToNumber("4960149153260042")
            .amount(new Amount(12920, "RUR"))
            .build();

    @Test
    void transactionTest_return200() {
        ResponseEntity<OperationDTO> response = restTemplate.postForEntity(
                "http://localhost:" + moneyTransferService.getMappedPort(5500) + "/transfer",
                validOperation,
                OperationDTO.class);
        System.out.println(response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());

        int operationId = Objects.requireNonNull(response.getBody()).operationId();
        ConfirmOperationDTO confirmOperationDTO;
        confirmOperationDTO = new ConfirmOperationDTO(operationId, "1234");

        response = restTemplate.postForEntity(
                "http://localhost:" + moneyTransferService.getMappedPort(5500) + "/confirmOperation",
                confirmOperationDTO,
                OperationDTO.class);

        assertThat(response.getStatusCode().value())
                .isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).operationId())
                .isEqualTo(operationId);

    }

    @Test
    void return400_invalidCard() {
        ResponseEntity<ErrorDTO> response = restTemplate.postForEntity(
                "http://localhost:" + moneyTransferService.getMappedPort(5500) + "/transfer",
                invalidCardInOperation,
                ErrorDTO.class);

        assertThat(response.getStatusCode().value())
                .isEqualTo(400);
        assertThat(Objects.requireNonNull(response.getBody()).message())
                .isNotEmpty()
                .matches("\\[cardFromNumber = \\w*] причина: Недопустимый номер карты отправителя");

    }

    @Test
    void return500_noOperationInRepo() {
        int opId = 123456789;
        ConfirmOperationDTO confirmOperationDTO = new ConfirmOperationDTO(opId, "1234");
        ResponseEntity<ErrorDTO> response = restTemplate.postForEntity(
                "http://localhost:" + moneyTransferService.getMappedPort(5500) + "/confirmOperation",
                confirmOperationDTO,
                ErrorDTO.class);

        assertThat(response.getStatusCode().value())
                .isEqualTo(500);

        assertThat(Objects.requireNonNull(response.getBody()).message())
                .isNotEmpty()
                .isEqualTo("Ошибка обработки операции: нет операции с id=" + opId);
    }

}
