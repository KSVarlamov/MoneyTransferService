package ru.netology.moneytransfer.exceptions;

import ru.netology.moneytransfer.model.CardToCardOperation;

public class OperationException extends RuntimeException {
    private final transient CardToCardOperation operation;

    public OperationException(String message, CardToCardOperation operation) {
        super(message);
        this.operation = operation;
    }

    public CardToCardOperation getOperation() {
        return operation;
    }
}
