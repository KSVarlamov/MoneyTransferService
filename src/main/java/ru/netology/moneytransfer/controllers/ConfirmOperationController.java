package ru.netology.moneytransfer.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.moneytransfer.DTO.ConfirmOperationDTO;
import ru.netology.moneytransfer.DTO.ErrorDTO;
import ru.netology.moneytransfer.DTO.OperationDTO;
import ru.netology.moneytransfer.exceptions.OperationNotFoundException;
import ru.netology.moneytransfer.service.ConfirmOperationService;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/")
@Slf4j
public class ConfirmOperationController {
    private final ConfirmOperationService service;

    public ConfirmOperationController(ConfirmOperationService service) {
        this.service = service;
    }

    static final Logger fileLogger = LoggerFactory.getLogger("OperationsConfirmLog");
    //TODO логирование в файл


    @PostMapping("confirmOperation")
    public OperationDTO handleRequest(@RequestBody @Valid ConfirmOperationDTO confirmOperationDTO) {
        log.info("Получен запрос на подтвержение операции перевода №{}", confirmOperationDTO.getOperationId());
        int id = service.confirm(confirmOperationDTO).getId();
        fileLogger.info("Обработан запрос на подтверждение {}", confirmOperationDTO);
        return new OperationDTO(id);

    }

    @ExceptionHandler(OperationNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleException(OperationNotFoundException exception) {
        fileLogger.warn("{}", exception.getMessage());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(exception.getId())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(500));
    }

}
