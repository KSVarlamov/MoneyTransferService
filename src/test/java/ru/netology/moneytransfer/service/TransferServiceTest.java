package ru.netology.moneytransfer.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.netology.moneytransfer.dto.CardToCardOperationDTO;
import ru.netology.moneytransfer.exceptions.OperationException;
import ru.netology.moneytransfer.model.Amount;
import ru.netology.moneytransfer.model.CardToCardOperation;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TransferServiceTest {

    @Autowired
    TransferService service;
    private final static Amount correctAmount = new Amount(31250, "RUR");
    private final static Amount amountWithUSD = new Amount(31250, "USD");
    private final static CardToCardOperationDTO correctOperation = new CardToCardOperationDTO(
            "4960144072893312",
            "11/23",
            "157",
            "4960149153260042",
            correctAmount
    );

    private final static CardToCardOperationDTO operationIncorrectCard = new CardToCardOperationDTO(
            "1234567",
            "11/23",
            "157",
            "4960149153260042",
            correctAmount
    );

    @Test
    public void transferMoney_correctData() {
        var operation = service.transferMoney(correctOperation);
        assertThat(operation.getCcFrom()).isEqualTo(correctOperation.cardFromNumber());
        assertThat(operation.getCcTo()).isEqualTo(correctOperation.cardToNumber());
        assertThat(operation.getStatus()).isEqualTo(CardToCardOperation.Status.WAITING_FOR_CONFIRM);
        assertThat(operation.getAmount()).isEqualTo(BigDecimal.valueOf(correctAmount.value() / 100.0));
        assertThat(operation.getCurrency()).isEqualTo(correctAmount.currency());
    }

    private final static CardToCardOperationDTO operationWithUSD = new CardToCardOperationDTO(
            "4960144072893312",
            "11/23",
            "157",
            "4960149153260042",
            amountWithUSD
    );

    @Test
    public void transferMoney_incorrectCard() {
        Throwable err = catchThrowable(() -> {
            service.transferMoney(operationIncorrectCard);
                });
        assertThat(err)
                .isInstanceOf(ConstraintViolationException.class);
        assertThat(err.getMessage())
                .endsWith("Недопустимый номер карты отправителя");
    }

    @Test
    public void transferMoney_NPE() {
        Throwable err = catchThrowable(() -> {
            service.transferMoney(null);
                });
        assertThat(err)
                .isInstanceOf(ConstraintViolationException.class);
        assertThat(err.getMessage())
                .endsWith("null");
    }

    @Test
    public void transferMoney_incorrectAmount() {
        Throwable err = catchThrowable(() -> {
            service.transferMoney(operationWithUSD);
        });
        assertThat(err)
                .isInstanceOf(OperationException.class);
        assertThat(err.getMessage())
                .endsWith("Доступны переводы только в рублях");
    }
}