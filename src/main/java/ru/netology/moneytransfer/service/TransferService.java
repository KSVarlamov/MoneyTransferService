package ru.netology.moneytransfer.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.netology.moneytransfer.dto.CardToCardOperationDTO;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.repository.OperationsRepository;
import ru.netology.moneytransfer.utils.CardToCardOperationChecker;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Validated
public class TransferService {
    private final AtomicInteger operationId = new AtomicInteger(0);
    private final OperationsRepository operationsRepository;

    private static final BigDecimal COMMISSION_PRICE = BigDecimal.valueOf(0.01);

    public TransferService(OperationsRepository operationsRepository) {
        this.operationsRepository = operationsRepository;
    }

    public CardToCardOperation transferMoney(@Valid @NotNull CardToCardOperationDTO operationDTO) {
        final var operation = new CardToCardOperation(operationId.incrementAndGet());
        operationsRepository.add(operation);

        var amountInRub = BigDecimal.valueOf((double) operationDTO.amount().value() / 100); //Переводим копейки в рубли
        operation.setAmount(amountInRub);
        operation.setCommission(amountInRub.multiply(COMMISSION_PRICE));

        CardToCardOperationChecker.checkOperation(operationDTO, operation);
        operation.setStatus(CardToCardOperation.Status.WAITING_FOR_CONFIRM);

        return operation;
    }
}
