package ru.netology.moneytransfer.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.netology.moneytransfer.dto.CardToCardOperationDTO;
import ru.netology.moneytransfer.dto.ConfirmOperationDTO;
import ru.netology.moneytransfer.dto.OperationDTO;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.service.ConfirmOperationService;
import ru.netology.moneytransfer.service.TransferService;

@CrossOrigin
@RestController
@RequestMapping("/")
@Slf4j
public class TransferController {

    private final TransferService transferService;
    private final ConfirmOperationService confirmOperationService;
    static final Logger fileLogger = LoggerFactory.getLogger("OperationsLog");

    public TransferController(TransferService transferService, ConfirmOperationService confirmOperationService) {
        this.transferService = transferService;
        this.confirmOperationService = confirmOperationService;
        fileLogger.info("Старт контроллера перевода денег");
    }

    @PostMapping("transfer")
    public OperationDTO transferMoney(@RequestBody @Valid CardToCardOperationDTO operation) {
        fileLogger.info("Получен запрос на проведение операции перевода [{}]", operation);
        var result = transferService.transferMoney(operation);
        fileLogger.info("Обработана операция перевода [{}]", result);
        return new OperationDTO(result.getId());
    }

    @PostMapping("confirmOperation")
    public OperationDTO handleRequest(@RequestBody @Valid ConfirmOperationDTO confirmOperationDTO) {
        fileLogger.info("Получен запрос на подтвержение операции перевода [{}]", confirmOperationDTO);
        var c2cOperation = confirmOperationService.confirm(confirmOperationDTO);
        fileLogger.info("Обработан запрос на подтвеждение операции {}", c2cOperation);
        return new OperationDTO(c2cOperation.getId());
    }

}
