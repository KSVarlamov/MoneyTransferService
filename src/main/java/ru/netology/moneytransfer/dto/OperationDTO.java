package ru.netology.moneytransfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperationDTO {
    private int operationId;

    public OperationDTO() {

    }
}
