package ru.netology.moneytransfer.exceptions;

import ru.netology.moneytransfer.model.CardToCardOperation;

public class CardNotValidException extends RuntimeException{
    private final transient CardToCardOperation operation;

    public CardNotValidException(String message, CardToCardOperation operation) {
        super(message);
        this.operation = operation;
    }

    public CardToCardOperation getOperation() {
        return operation;
    }
}
