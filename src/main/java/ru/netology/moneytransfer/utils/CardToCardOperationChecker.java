package ru.netology.moneytransfer.utils;

import ru.netology.moneytransfer.dto.CardToCardOperationDTO;
import ru.netology.moneytransfer.exceptions.CardNotValidException;
import ru.netology.moneytransfer.exceptions.OperationException;
import ru.netology.moneytransfer.model.CardToCardOperation;

import java.time.LocalDate;

public class CardToCardOperationChecker {

    private CardToCardOperationChecker() {
        throw new IllegalStateException("Utility class");
    }

    public static void checkOperation(CardToCardOperationDTO operationDTO, CardToCardOperation operation) {
        if (operationDTO.cardFromNumber().equals(operationDTO.cardToNumber())) {
            var err = String.format("Ошибка обработки операции: Нельзя отправить деньги самому себе [%s]", operationDTO.cardFromNumber());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new OperationException(err, operation);
        } else {
            operation.setCcFrom(operationDTO.cardFromNumber());
            operation.setCcTo(operationDTO.cardToNumber());
        }
        operation.setCurrency(operationDTO.amount().currency());
        if (!"RUR".equals(operationDTO.amount().currency())) {
            var err = "Ошибка обработки операции: Доступны переводы только в рублях";
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new OperationException(err, operation);
        }
        String[] parts = operationDTO.cardFromValidTill().split("/");
        int month = Integer.parseInt(parts[0]);
        if (month > 12 || month <= 0) {
            var err = String.format("Ошибка обработки операции: Некорректная дата действия карты [%s]. Месяц в диапазоне должен быть 01..12", operationDTO.cardFromValidTill());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotValidException(err, operation);
        }
        LocalDate date = LocalDate.now();
        var cardYear = Integer.parseInt(parts[1]) + 2000;
        if ((cardYear < date.getYear()) || ((cardYear == date.getYear()) && (month < date.getMonthValue()))) {
            var err = String.format("Ошибка обработки операции: Дата действия карты [%s] истекла", operationDTO.cardFromValidTill());
            operation.setStatus(CardToCardOperation.Status.FAILED);
            operation.setReason(err);
            throw new CardNotValidException(err, operation);
        }
    }
}