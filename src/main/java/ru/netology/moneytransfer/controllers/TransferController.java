package ru.netology.moneytransfer.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.netology.moneytransfer.DTO.CardToCardOperationDTO;
import ru.netology.moneytransfer.DTO.ErrorDTO;
import ru.netology.moneytransfer.DTO.OperationDTO;
import ru.netology.moneytransfer.exceptions.CardNotFoundException;
import ru.netology.moneytransfer.exceptions.CardNotValidException;
import ru.netology.moneytransfer.service.TransferService;

import java.util.stream.Collectors;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/")
@Slf4j
public class TransferController {

    private final TransferService service;
    static final Logger fileLogger = LoggerFactory.getLogger("OperationsLog");

    public TransferController(TransferService service) {
        this.service = service;
        log.info("Старт контроллера перевода денег");
    }

    @PostMapping("transfer")
    public OperationDTO transferMoney(@RequestBody @Valid CardToCardOperationDTO operation) {
        fileLogger.info("Получен запрос на проведение операции {}", operation);
        var result = service.transferMoney(operation);
        fileLogger.info("Обработана операция {}", result);
        return new OperationDTO(result.getId());
    }

    @ExceptionHandler(CardNotValidException.class)
    public ResponseEntity<ErrorDTO> handleException(CardNotValidException exception) {
        fileLogger.warn("{} [{}]", exception.getMessage(), exception.getOperation());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(exception.getOperation().getId())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleException(CardNotFoundException exception) {
        fileLogger.warn("{} [{}]", exception.getMessage(), exception.getOperation());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(exception.getOperation().getId())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(500));
    }

}
