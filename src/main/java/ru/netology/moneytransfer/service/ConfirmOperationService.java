package ru.netology.moneytransfer.service;

import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.exceptions.OperationNotFoundException;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.DTO.ConfirmOperationDTO;
import ru.netology.moneytransfer.repository.OperationsRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ConfirmOperationService {

    private final OperationsRepository operationsRepository;
    private static final BigDecimal COMMISSION_PRICE = BigDecimal.valueOf(1.01);

    public ConfirmOperationService(OperationsRepository operationsRepository) {
        this.operationsRepository = operationsRepository;
    }



    public synchronized CardToCardOperation confirm(ConfirmOperationDTO operationDTO) {

        Optional<CardToCardOperation> operation = operationsRepository.getById(operationDTO.getOperationId());
        if (operation.isEmpty()) {
            throw new OperationNotFoundException("No operation with id " + operationDTO.getOperationId(), operationDTO.getOperationId());
        }

        if (!operation.get().getStatus().equals(CardToCardOperation.Status.WAITING_FOR_CONFIRM)) {
            throw new OperationNotFoundException("Операция не требует подтверждения " + operationDTO.getOperationId(), operationDTO.getOperationId());
        }

        if (!operation.get().checkCode(operationDTO.getCode())) {
            throw new OperationNotFoundException("Некорректный код подтверждения " + operationDTO.getCode(), operationDTO.getOperationId());
        }


        BigDecimal amount = BigDecimal.valueOf(operation.get().getAmount().getValue());
        amount = amount.divide(BigDecimal.valueOf(100)); //копейки в рубли
        BigDecimal amountWithCommission = amount.multiply(COMMISSION_PRICE);

        if (operation.get().getCcFrom().spendMoney(amountWithCommission)) {
            operation.get().getCcTo().addMoney(amount);
            operation.get().setStatus(CardToCardOperation.Status.DONE);
        } else {
            operation.get().setStatus(CardToCardOperation.Status.FAILED);
            throw new OperationNotFoundException("Недостаточно денег для проведения опереации " + operationDTO.getOperationId(), operationDTO.getOperationId());
        }

        return operation.get();
    }
}
