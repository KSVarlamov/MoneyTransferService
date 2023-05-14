package ru.netology.moneytransfer.repository;

import org.springframework.stereotype.Repository;
import ru.netology.moneytransfer.model.CardToCardOperation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OperationsRepositoryInMemory implements OperationsRepository {
    private final Map<Integer, CardToCardOperation> operations = new ConcurrentHashMap<>();

    @Override
    public void add(CardToCardOperation operation) {
        operations.put(operation.getId(), operation);
    }

    @Override
    public boolean delete(CardToCardOperation operation) {
        return operations.remove(operation.getId(), operation);
    }

    @Override
    public void deleteAll() {
        operations.clear();
    }

    @Override
    public Optional<CardToCardOperation> getById(int id) {
        return Optional.ofNullable(operations.get(id));
    }
}
