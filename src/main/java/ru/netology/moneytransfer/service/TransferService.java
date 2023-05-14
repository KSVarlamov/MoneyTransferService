package ru.netology.moneytransfer.service;

import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.dto.CardToCardOperationDTO;
import ru.netology.moneytransfer.exceptions.CardNotValidException;
import ru.netology.moneytransfer.exceptions.OperationException;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.repository.OperationsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TransferService {
    private static final AtomicInteger operationId = new AtomicInteger(0);
    private final OperationsRepository operationsRepository;

    private static final BigDecimal COMMISSION_PRICE = BigDecimal.valueOf(0.01);

    public TransferService(OperationsRepository operationsRepository) {
        this.operationsRepository = operationsRepository;
    }

    public CardToCardOperation transferMoney(CardToCardOperationDTO operationDTO) {
        final CardToCardOperation operation = new CardToCardOperation(operationId.incrementAndGet());
        operationsRepository.add(operation);

        BigDecimal amountInRub = BigDecimal.valueOf((double) operationDTO.amount().getValue() / 100); //Переводим копейки в рубли
        operation.setAmount(amountInRub);
        operation.setCommission(amountInRub.multiply(COMMISSION_PRICE));

        checkOperation(operationDTO, operation);
        operation.setStatus(CardToCardOperation.Status.WAITING_FOR_CONFIRM);

        return operation;
    }

    private void checkOperation(CardToCardOperationDTO operationDTO, CardToCardOperation operation) {
        if (operationDTO.cardFromNumber().equals(operationDTO.cardToNumber())) {
            String err = String.format("Ошибка обработки операции: Нельзя отправить деньги самому себе [%s]", operationDTO.cardFromNumber());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new OperationException(err, operation);
        } else {
            operation.setCcFrom(operationDTO.cardFromNumber());
            operation.setCcTo(operationDTO.cardToNumber());
        }
        operation.setCurrency(operationDTO.amount().getCurrency());
        if (!"RUR".equals(operationDTO.amount().getCurrency())) {
            String err = "Ошибка обработки операции: Доступны переводы только в рублях";
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new OperationException(err, operation);
        }
        String[] parts = operationDTO.cardFromValidTill().split("/");
        int month = Integer.parseInt(parts[0]);
        if (month > 12 || month <= 0) {
            String err = String.format("Ошибка обработки операции: Некорректная дата действия карты [%s]. Месяц в диапазоне должен быть 01..12", operationDTO.cardFromValidTill());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotValidException(err, operation);
        }
        LocalDate date = LocalDate.now();
        int cardYear = Integer.parseInt(parts[1]) + 2000;
        if ((cardYear < date.getYear()) || ((cardYear == date.getYear()) && (month < date.getMonthValue()))) {
            String err = String.format("Ошибка обработки операции: Дата действия карты [%s] истекла", operationDTO.cardFromValidTill());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotValidException(err, operation);
        }
    }

}
