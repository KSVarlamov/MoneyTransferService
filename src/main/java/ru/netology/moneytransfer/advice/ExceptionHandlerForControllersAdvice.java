package ru.netology.moneytransfer.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.moneytransfer.dto.ErrorDTO;
import ru.netology.moneytransfer.exceptions.CardNotValidException;
import ru.netology.moneytransfer.exceptions.OperationException;
import ru.netology.moneytransfer.exceptions.OperationNotFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerForControllersAdvice {

    public static final String ERROR_MESSAGE_TEMPLATE = "Некорректеный запрос: {}";
    static final Logger fileLogger = LoggerFactory.getLogger("OperationsLog");

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleException(HttpMessageNotReadableException exception) {
        fileLogger.warn(ERROR_MESSAGE_TEMPLATE, exception.getMessage());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(-1)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream().map(p -> "[" + p.getField() + " = " + p.getRejectedValue() + "] причина: " + p.getDefaultMessage()).collect(Collectors.joining(";   "));
        fileLogger.warn(ERROR_MESSAGE_TEMPLATE, errorMessage);
        ErrorDTO error = ErrorDTO.builder().operationId(-1).message(errorMessage).build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(400));
    }


    @ExceptionHandler(CardNotValidException.class)
    public ResponseEntity<ErrorDTO> handleException(CardNotValidException exception) {
        fileLogger.warn(ERROR_MESSAGE_TEMPLATE, exception.getOperation());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(exception.getOperation().getId())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(OperationException.class)
    public ResponseEntity<ErrorDTO> handleException(OperationException exception) {
        fileLogger.warn("Ошибка обработки операции [{}]", exception.getOperation());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(exception.getOperation().getId())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(500));
    }


    @ExceptionHandler(OperationNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleException(OperationNotFoundException exception) {
        fileLogger.warn("Некорректеный запрос: {}", exception.getMessage());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(exception.getId())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(500));
    }
}
