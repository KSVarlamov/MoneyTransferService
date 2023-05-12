package ru.netology.moneytransfer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.moneytransfer.model.Amount;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.repository.OperationsRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConfirmOperationControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    OperationsRepository operationsRepository;

    private static CardToCardOperation testC2COperation1;

    @BeforeEach
    void addData() {
        testC2COperation1 = new CardToCardOperation(1);
        testC2COperation1.setStatus(CardToCardOperation.Status.WAITING_FOR_CONFIRM);
        testC2COperation1.setCcFrom("4960144072893312");
        testC2COperation1.setCcTo("4960149153260042");
        testC2COperation1.setCode("0123");
        testC2COperation1.setAmount(BigDecimal.valueOf(5000));
        Mockito.when(operationsRepository.getById(1)).thenReturn(Optional.of(testC2COperation1));
    }

    @Test
    void returnOk() throws Exception {
        String requestBody = """
                {
                  "code": "0123",
                  "operationId": "1"
                }
                """;
        mockMvc.perform(
                        post("/confirmOperation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").value(1));
        Assertions.assertEquals(CardToCardOperation.Status.DONE, testC2COperation1.getStatus());
    }


    @Test
    void return400_idNegative() throws Exception {
        String requestBody = """
                {
                  "code": "0123",
                  "operationId": "-5"
                }
                """;
        mockMvc.perform(
                        post("/confirmOperation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.operationId").value(-1))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void return400_idIsString() throws Exception {
        String requestBody = """
                {
                  "code": "0123",
                  "operationId": "AAA"
                }
                """;
        mockMvc.perform(
                        post("/confirmOperation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.operationId").value(-1))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }


    @Test
    void return400_idIsDouble() throws Exception {
        String requestBody = """
                {
                  "code": "0123",
                  "operationId": "13.5"
                }
                """;
        mockMvc.perform(
                        post("/confirmOperation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.operationId").value(-1))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void return500_incorrectCode() throws Exception {
        String requestBody = """
                {
                  "code": "0987",
                  "operationId": "1"
                }
                """;
        mockMvc.perform(
                        post("/confirmOperation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.operationId").value(1));
        Assertions.assertEquals(CardToCardOperation.Status.WAITING_FOR_CONFIRM, testC2COperation1.getStatus());


        requestBody = """
                {
                  "code": "0987",
                  "operationId": "1"
                }
                """;
        mockMvc.perform(
                        post("/confirmOperation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.operationId").value(1));
        Assertions.assertEquals(CardToCardOperation.Status.WAITING_FOR_CONFIRM, testC2COperation1.getStatus());

        requestBody = """
                {
                  "code": "0987",
                  "operationId": "1"
                }
                """;
        mockMvc.perform(
                        post("/confirmOperation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.operationId").value(1));
        Assertions.assertEquals(CardToCardOperation.Status.FAILED, testC2COperation1.getStatus());

    }

}
