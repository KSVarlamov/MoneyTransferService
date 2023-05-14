package ru.netology.moneytransfer.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record Amount(
        @Min(value = 0, message = "Сумма перевода должна быть больше 0") int value,
        @NotBlank @Length(min = 1, max = 3, message = "Название валюты должно быть 1..3 символа") String currency) {
}
