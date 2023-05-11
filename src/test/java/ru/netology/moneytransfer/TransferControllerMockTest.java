package ru.netology.moneytransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.moneytransfer.model.CreditCard;
import ru.netology.moneytransfer.repository.CCardRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerMockTest {

    @MockBean
    CCardRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void addTestData() {
        CreditCard cc1 = new CreditCard("4960144072893312", "157", "11/23", new BigDecimal(1000));
        CreditCard cc2 = new CreditCard("4960149153260042", "333", "01/24", new BigDecimal(0));
        Mockito.when(repository.getCardByNumber("4960144072893312")).thenReturn(Optional.of(cc1));
        Mockito.when(repository.getCardByNumber("4960149153260042")).thenReturn(Optional.of(cc2));
    }

    @Test
    void returnOk() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": 129200
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").isNumber());
    }

    @Test
    void incorrectCardFromNumberTest() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "321",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": 3500000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    @Test
    void incorrectCardToNumberTest() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "321",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void incorrectCVVNumberTest_2digits() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "03",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    @Test
    void incorrectCVVNumberTest_5letters() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "03",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void incorrectTill_someText() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "355",
                  "cardFromValidTill": "asdfsad",
                  "amount": {
                    "currency": "RUR",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void incorrectTill_monthOver12() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "355",
                  "cardFromValidTill": "13/25",
                  "amount": {
                    "currency": "RUR",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    @Test
    void incorrectTill_month0() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "355",
                  "cardFromValidTill": "0/25",
                  "amount": {
                    "currency": "RUR",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void incorrectTill_expireDate() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "355",
                  "cardFromValidTill": "04/23",
                  "amount": {
                    "currency": "RUR",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void cardFromNotInRepo() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960145176714874",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void cardToNotInRepo() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960143468869167",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    //TODO amoumt tests


    @Test
    void amount_nonRur() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960143468869167",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "USD",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void amount_incorrectCurrency() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960143468869167",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "23423423423423426",
                    "value": 35000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void amount_negativeValue() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960143468869167",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "23423423423423426",
                    "value": -50000
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void amount_valueIsString() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960143468869167",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": "asf"
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void transfer_himself() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960144072893312",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": "50000"
                  }
                }
                """;

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
