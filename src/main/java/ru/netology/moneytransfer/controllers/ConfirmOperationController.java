package ru.netology.moneytransfer.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.netology.moneytransfer.dto.ConfirmOperationDTO;
import ru.netology.moneytransfer.dto.OperationDTO;
import ru.netology.moneytransfer.model.CardToCardOperation;
import ru.netology.moneytransfer.service.ConfirmOperationService;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/")
@Slf4j
public class ConfirmOperationController {
    static final Logger fileLogger = LoggerFactory.getLogger("OperationsLog");
    private final ConfirmOperationService service;

    public ConfirmOperationController(ConfirmOperationService service) {
        this.service = service;
        fileLogger.info("Старт контроллера подтверждения операция");
    }

    @PostMapping("confirmOperation")
    public OperationDTO handleRequest(@RequestBody @Valid ConfirmOperationDTO confirmOperationDTO) {
        fileLogger.info("Получен запрос на подтвержение операции перевода [{}]", confirmOperationDTO);
        CardToCardOperation c2cOperation = service.confirm(confirmOperationDTO);
        fileLogger.info("Обработан запрос на подтвеждение операции {}", c2cOperation);
        return new OperationDTO(c2cOperation.getId());
    }

}
