package ru.netology.moneytransfer.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.netology.moneytransfer.dto.ConfirmOperationDTO;
import ru.netology.moneytransfer.exceptions.OperationNotFoundException;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.repository.OperationsRepository;

@Service
@Validated
public class ConfirmOperationService {

    private final OperationsRepository operationsRepository;

    public ConfirmOperationService(OperationsRepository operationsRepository) {
        this.operationsRepository = operationsRepository;
    }

    public synchronized CardToCardOperation confirm(@Valid @NotNull ConfirmOperationDTO operationDTO) {

        var operation = operationsRepository.getById(operationDTO.operationId());
        if (operation.isEmpty()) {
            throw new OperationNotFoundException("Ошибка обработки операции: нет операции с id=" + operationDTO.operationId(), operationDTO.operationId());
        }

        if (!operation.get().getStatus().equals(CardToCardOperation.Status.WAITING_FOR_CONFIRM)) {
            throw new OperationNotFoundException("Ошибка обработки операции: Операция не ожидает подтверждения " + operationDTO.operationId(), operationDTO.operationId());
        }
        operation.get().setCode(operationDTO.code());
        operation.get().setStatus(CardToCardOperation.Status.DONE);
        return operation.get();
    }
}
