package ru.netology.moneytransfer.service;

import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.dto.ConfirmOperationDTO;
import ru.netology.moneytransfer.exceptions.OperationNotFoundException;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.repository.OperationsRepository;

import java.util.Optional;

@Service
public class ConfirmOperationService {

    private final OperationsRepository operationsRepository;

    public ConfirmOperationService(OperationsRepository operationsRepository) {
        this.operationsRepository = operationsRepository;
    }

    public synchronized CardToCardOperation confirm(ConfirmOperationDTO operationDTO) {

        Optional<CardToCardOperation> operation = operationsRepository.getById(operationDTO.getOperationId());
        if (operation.isEmpty()) {
            throw new OperationNotFoundException("Ошибка обработки операции: нет операции с id=" + operationDTO.getOperationId(), operationDTO.getOperationId());
        }

        if (!operation.get().getStatus().equals(CardToCardOperation.Status.WAITING_FOR_CONFIRM)) {
            throw new OperationNotFoundException("Ошибка обработки операции: Операция не ожидает подтверждения " + operationDTO.getOperationId(), operationDTO.getOperationId());
        }
        operation.get().setCode(operationDTO.getCode());
        operation.get().setStatus(CardToCardOperation.Status.DONE);
        return operation.get();
    }
}
