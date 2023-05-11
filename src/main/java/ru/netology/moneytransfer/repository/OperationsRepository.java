package ru.netology.moneytransfer.repository;

import ru.netology.moneytransfer.model.CardToCardOperation;

import java.util.Optional;

public interface OperationsRepository {
    void add(CardToCardOperation operation);
    boolean delete(CardToCardOperation operation);
    void deleteAll();
    Optional<CardToCardOperation> getById(int id);
}
