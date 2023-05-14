package ru.netology.moneytransfer.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.moneytransfer.dto.ErrorDTO;
import ru.netology.moneytransfer.exceptions.CardNotValidException;
import ru.netology.moneytransfer.exceptions.OperationException;
import ru.netology.moneytransfer.exceptions.OperationNotFoundException;

@RestControllerAdvice
public class ExceptionHandlerForControllersAdvice {

    public static final String RESPONSE_ERROR_MESSAGE_TEMPLATE = "Некорректеный запрос: [{}]";
    public static final String OPERATION_ERROR_MESSAGE_TEMPLATE = "Ошибка обработки операции [{}]";
    private static final Logger fileLogger = LoggerFactory.getLogger("OperationsLog");

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleException(HttpMessageNotReadableException exception) {
        fileLogger.error(RESPONSE_ERROR_MESSAGE_TEMPLATE, exception.getMessage());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(-1)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleException(MethodArgumentNotValidException exception) {
        var errorMessage = new StringBuilder();
        var errors = exception.getBindingResult().getFieldErrors();
        for (int i = 0; i < errors.size(); i++) {
            errorMessage.append("[")
                    .append(errors.get(i).getField())
                    .append(" = ")
                    .append(errors.get(i).getRejectedValue())
                    .append("] причина: ")
                    .append(errors.get(i).getDefaultMessage());
                    if (i + 1 < errors.size()) {
                        errorMessage.append(";   ");
                    }
        }
        fileLogger.error(RESPONSE_ERROR_MESSAGE_TEMPLATE, errorMessage);
        ErrorDTO error = ErrorDTO.builder().operationId(-1).message(errorMessage.toString()).build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(CardNotValidException.class)
    public ResponseEntity<ErrorDTO> handleException(CardNotValidException exception) {
        fileLogger.error(RESPONSE_ERROR_MESSAGE_TEMPLATE, exception.getOperation());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(exception.getOperation().getId())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OperationException.class)
    public ResponseEntity<ErrorDTO> handleException(OperationException exception) {
        fileLogger.error(OPERATION_ERROR_MESSAGE_TEMPLATE, exception.getOperation());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(exception.getOperation().getId())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(OperationNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleException(OperationNotFoundException exception) {
        fileLogger.error(OPERATION_ERROR_MESSAGE_TEMPLATE, exception.getMessage());
        ErrorDTO error = ErrorDTO.builder()
                .operationId(exception.getId())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
