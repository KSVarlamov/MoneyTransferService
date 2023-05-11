package ru.netology.moneytransfer.exceptions;

import ru.netology.moneytransfer.model.CardToCardOperation;

public class CardNotFoundException extends RuntimeException {
    private final CardToCardOperation operation;

    public CardNotFoundException(String message, CardToCardOperation operation) {
        super(message);
        this.operation = operation;
    }

    public CardToCardOperation getOperation() {
        return operation;
    }
}
