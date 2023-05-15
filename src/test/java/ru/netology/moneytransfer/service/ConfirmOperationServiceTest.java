package ru.netology.moneytransfer.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.netology.moneytransfer.controllers.TransferController;
import ru.netology.moneytransfer.dto.ConfirmOperationDTO;
import ru.netology.moneytransfer.exceptions.OperationNotFoundException;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.repository.OperationsRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
@RunWith(SpringRunner.class)
class ConfirmOperationServiceTest {

    @Autowired
    ConfirmOperationService service;

    @Autowired
    TransferController controller;

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

    private final static ConfirmOperationDTO c2cOpValid = new ConfirmOperationDTO(1, "0123");
    private final static ConfirmOperationDTO c2cOpWithNegativeId = new ConfirmOperationDTO(-25, "0123");
    private final static ConfirmOperationDTO c2cOpWithInvalidCodeLength = new ConfirmOperationDTO(1, "asdfasdfasdf");
    private final static ConfirmOperationDTO c2cOpNotInRepo = new ConfirmOperationDTO(158, "3210");

    @Test
    void confirm_validData() {
        var operation = service.confirm(c2cOpValid);
        assertThat(operation)
                .isEqualTo(testC2COperation1);
    }

    @Test
    void confirm_idNegative() {
        Throwable err = catchThrowable(() -> {
            service.confirm(c2cOpWithNegativeId);
        });
        assertThat(err)
                .isInstanceOf(ConstraintViolationException.class);
        assertThat(err.getMessage())
                .endsWith("должно быть больше 0");
    }

    @Test
    void confirm_invalidCode() throws Exception {
        Throwable err = catchThrowable(() -> {
            service.confirm(c2cOpWithInvalidCodeLength);
        });
        assertThat(err)
                .isInstanceOf(ConstraintViolationException.class);
        assertThat(err.getMessage())
                .endsWith("длина должна составлять от 4 до 6");
    }

    @Test
    void confirm_operationNotInRepo() throws Exception {
        Throwable err = catchThrowable(() -> {
            service.confirm(c2cOpNotInRepo);
        });
        assertThat(err)
                .isInstanceOf(OperationNotFoundException.class);
        assertThat(err.getMessage())
                .endsWith("id=" + c2cOpNotInRepo.operationId());
    }

    @Test
    void confirm_NPE() throws Exception {
        Throwable err = catchThrowable(() -> {
            service.confirm(null);
        });
        assertThat(err)
                .isInstanceOf(ConstraintViolationException.class);
        assertThat(err.getMessage())
                .endsWith("null");
    }
}