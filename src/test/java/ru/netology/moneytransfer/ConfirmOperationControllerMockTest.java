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
import ru.netology.moneytransfer.model.CreditCard;
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
    private final static Amount normalAmount = new Amount(50000, "RUR");
    private final static Amount biglAmount = new Amount(170000, "RUR");


    @BeforeEach
    void addData() {
        testC2COperation1 = new CardToCardOperation(1);
        CreditCard cc1 = new CreditCard("4960144072893312", "157", "11/23", new BigDecimal(1500));
        CreditCard cc2 = new CreditCard("4960149153260042", "333", "01/24", new BigDecimal(0));
        testC2COperation1.setStatus(CardToCardOperation.Status.WAITING_FOR_CONFIRM);
        testC2COperation1.setCcFrom(cc1);
        testC2COperation1.setCcTo(cc2);
        testC2COperation1.setCode("0123");
        testC2COperation1.setAmount(normalAmount);
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
        Assertions.assertEquals(0, testC2COperation1.getCcFrom().getBalance().compareTo(new BigDecimal(995)));
        Assertions.assertEquals(0, testC2COperation1.getCcTo().getBalance().compareTo(new BigDecimal(500)));
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
        Assertions.assertEquals(0, testC2COperation1.getCcFrom().getBalance().compareTo(new BigDecimal(1500)));
        Assertions.assertEquals(0, testC2COperation1.getCcTo().getBalance().compareTo(new BigDecimal(0)));

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
        Assertions.assertEquals(0, testC2COperation1.getCcFrom().getBalance().compareTo(new BigDecimal(1500)));
        Assertions.assertEquals(0, testC2COperation1.getCcTo().getBalance().compareTo(new BigDecimal(0)));

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
        Assertions.assertEquals(0, testC2COperation1.getCcFrom().getBalance().compareTo(new BigDecimal(1500)));
        Assertions.assertEquals(0, testC2COperation1.getCcTo().getBalance().compareTo(new BigDecimal(0)));

    }

    @Test
    void return500_noMoney() throws Exception {
        String requestBody = """
                {
                  "code": "0123",
                  "operationId": "1"
                }
                """;
        testC2COperation1.setAmount(biglAmount);
        mockMvc.perform(
                        post("/confirmOperation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.operationId").value(1));
        Assertions.assertEquals(0, testC2COperation1.getCcFrom().getBalance().compareTo(new BigDecimal(1500)));
        Assertions.assertEquals(0, testC2COperation1.getCcTo().getBalance().compareTo(new BigDecimal(0)));
        Assertions.assertEquals(CardToCardOperation.Status.FAILED, testC2COperation1.getStatus());
    }

}
