package ru.netology.moneytransfer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.moneytransfer.repository.OperationsRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    OperationsRepository operationsRepository;

    @Test
    void return200_correctData() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "4960144072893312",
                  "cardToNumber": "4960149153260042",
                  "cardFromCVV": "157",
                  "cardFromValidTill": "11/23",
                  "amount": {
                    "currency": "RUR",
                    "value": 12920
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
    void return400_incorrectCardFromNumberTest() throws Exception {
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
    void return400_overOneIncorrectField() throws Exception {
        String requestBody = """
                {
                  "cardFromNumber": "321",
                  "cardToNumber": "321",
                  "cardFromCVV": "zz",
                  "cardFromValidTill": "11/19",
                  "amount": {
                    "currency": "RUR",
                    "value": 300000
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
    void return400_incorrectCardToNumberTest() throws Exception {
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
    void return400_incorrectCVVNumberTest_2digits() throws Exception {
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
    void return400_incorrectCVVNumberTest_5letters() throws Exception {
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
    void return400_incorrectTill_someText() throws Exception {
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
    void return400_incorrectTill_monthOver12() throws Exception {
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
    void return400_incorrectTill_month0() throws Exception {
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
    void return400_incorrectTill_expireDate() throws Exception {
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
    void return500_amount_nonRur() throws Exception {
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
    void return400_amount_incorrectCurrency() throws Exception {
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
    void return400_amount_negativeValue() throws Exception {
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
    void return400_amount_valueIsString() throws Exception {
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
    void return500_transfer_himself() throws Exception {
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
