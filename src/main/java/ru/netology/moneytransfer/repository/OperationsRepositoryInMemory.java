package ru.netology.moneytransfer.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.netology.moneytransfer.model.CardToCardOperation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Validated
public class OperationsRepositoryInMemory implements OperationsRepository {
    private final Map<Integer, CardToCardOperation> operations = new ConcurrentHashMap<>();

    @Override
    public void add(@NotNull @Valid CardToCardOperation operation) {
        operations.put(operation.getId(), operation);
    }

    @Override
    public boolean delete(@NotNull CardToCardOperation operation) {
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
