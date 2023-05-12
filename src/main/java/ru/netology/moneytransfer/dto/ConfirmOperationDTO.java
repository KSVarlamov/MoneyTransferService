package ru.netology.moneytransfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ConfirmOperationDTO {

    @Positive
    int operationId;

    @NotBlank
    @Length(min = 4, max = 6)
    String code;
}
