package ru.netology.moneytransfer.service;

import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.dto.CardToCardOperationDTO;
import ru.netology.moneytransfer.exceptions.CardNotFoundException;
import ru.netology.moneytransfer.exceptions.CardNotValidException;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.model.CreditCard;
import ru.netology.moneytransfer.repository.CCardRepository;
import ru.netology.moneytransfer.repository.OperationsRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TransferService {
    private static final AtomicInteger operationId = new AtomicInteger(0);
    private final CCardRepository ccRepository;
    private final OperationsRepository operationsRepository;

    public TransferService(CCardRepository ccRepository, OperationsRepository operationsRepository) {
        this.ccRepository = ccRepository;
        this.operationsRepository = operationsRepository;
    }

    public CardToCardOperation transferMoney(CardToCardOperationDTO operationDTO) {
        final CardToCardOperation operation = new CardToCardOperation(operationId.incrementAndGet());
        operationsRepository.add(operation);

        checkOperation(operationDTO, operation);

        operation.setAmount(operationDTO.getAmount());

        operation.setStatus(CardToCardOperation.Status.WAITING_FOR_CONFIRM);

        return operation;
    }

    private void checkOperation(CardToCardOperationDTO operationDTO, CardToCardOperation operation) {
        if (operationDTO.getCardFromNumber().equals(operationDTO.getCardToNumber())) {
            String err = String.format("Ошибка обработки операции: Нельзя отправить деньги самому себе [%s]", operationDTO.getCardFromNumber());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotFoundException(err, operation);
        }

        if (!"RUR".equals(operationDTO.getAmount().getCurrency())) {
            String err = "Ошибка обработки операции: Доступны переводы только в рублях";
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotFoundException(err, operation);
        }

        String[] parts = operationDTO.getCardFromValidTill().split("/");

        int month = Integer.parseInt(parts[0]);
        if (month > 12 || month <= 0) {
            String err = String.format("Ошибка обработки операции: Некорректная дата действия карты [%s]. Месяц в диапазоне должен быть 01..12", operationDTO.getCardFromValidTill());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotValidException(err, operation);
        }

        LocalDate date = LocalDate.now();

        int cardYear = Integer.parseInt(parts[1]) + 2000;
        if ((cardYear < date.getYear()) || ((cardYear == date.getYear()) && (month < date.getMonthValue()))) {
            String err = String.format("Ошибка обработки операции: Дата действия карты [%s] истекла", operationDTO.getCardFromValidTill());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotValidException(err, operation);
        }

        Optional<CreditCard> ccFrom = ccRepository.getCardByNumber(operationDTO.getCardFromNumber());
        if (ccFrom.isEmpty()) {
            String err = String.format("Ошибка обработки операции: Карта отправителя [%s] не существует", operationDTO.getCardFromNumber());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotFoundException(err, operation);
        } else {
            operation.setCcFrom(ccFrom.get());
        }
        Optional<CreditCard> ccTo = ccRepository.getCardByNumber(operationDTO.getCardToNumber());
        if (ccTo.isEmpty()) {
            String err = String.format("Ошибка обработки операции: Карта получателя [%s] не существует", operationDTO.getCardToNumber());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotFoundException(err, operation);
        } else {
            operation.setCcTo(ccFrom.get());
        }

        if (!operationDTO.getCardFromValidTill().equals(ccFrom.get().getCcTill())) {
            String err = String.format("Ошибка обработки операции: Некорректная дата действия карты [%s]", operationDTO.getCardFromValidTill());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotFoundException(err, operation);
        }

        if (!operationDTO.getCardFromCVV().equals(ccFrom.get().getCvv())) {
            String err = String.format("Ошибка обработки операции: Некорректный CVV [%s]", operationDTO.getCardFromCVV());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotFoundException(err, operation);
        }
    }


}
