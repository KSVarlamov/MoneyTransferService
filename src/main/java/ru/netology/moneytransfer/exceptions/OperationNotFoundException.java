package ru.netology.moneytransfer.exceptions;

public class OperationNotFoundException extends RuntimeException {
    private final int id;

    public OperationNotFoundException(String message, int id) {
        super(message);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
