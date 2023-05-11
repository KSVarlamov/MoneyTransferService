package ru.netology.moneytransfer.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.moneytransfer.DTO.ErrorDTO;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerForControllersAdvice {

    static final Logger fileLogger = LoggerFactory.getLogger("OperationsConfirmLog");

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleException(HttpMessageNotReadableException exception) {
        fileLogger.warn("{}", exception.getMessage());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(-1)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream().map(p -> "[" + p.getField() + " = " + p.getRejectedValue() + "] причина: " + p.getDefaultMessage()).collect(Collectors.joining(";   "));
        fileLogger.warn("Некоррктеный запрос на подтвердение операции {}", errorMessage);
        ErrorDTO error = ErrorDTO.builder().operationId(-1).message(errorMessage).build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(400));
    }
}
