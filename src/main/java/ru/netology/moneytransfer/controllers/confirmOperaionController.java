package ru.netology.moneytransfer.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.netology.moneytransfer.DTO.ConfirmOperationDTO;
import ru.netology.moneytransfer.DTO.ErrorDTO;
import ru.netology.moneytransfer.DTO.OperationDTO;
import ru.netology.moneytransfer.exceptions.OperationNotFoundException;
import ru.netology.moneytransfer.service.ConfirmOperationService;

import java.util.stream.Collectors;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/")
@Slf4j
public class confirmOperaionController {
    private final ConfirmOperationService service;

    public confirmOperaionController(ConfirmOperationService service) {
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream().map(p -> "[" + p.getField() + " = " + p.getRejectedValue() + "] причина: " + p.getDefaultMessage()).collect(Collectors.joining(";   "));
        fileLogger.warn("Некоррктеный запрос на подтвердение операции {}", errorMessage);
        ErrorDTO error = ErrorDTO.builder().operationId(-1).message(errorMessage).build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(400));
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

    //TODO вынести в отдельный класс общие ошибки
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleException(HttpMessageNotReadableException exception) {
        fileLogger.warn("{}", exception.getMessage());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(-1)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(400));
    }
}
