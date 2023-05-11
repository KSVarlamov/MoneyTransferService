package ru.netology.moneytransfer.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
public class ConfirmOperationDTO {

    @Positive
    int operationId;

    @NotBlank
    @Length(min = 4, max = 6)
    String code;
}
