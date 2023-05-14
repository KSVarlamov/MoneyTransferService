package ru.netology.moneytransfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;


public record ConfirmOperationDTO(@Positive int operationId, @NotBlank @Length(min = 4, max = 6) String code) {
}

