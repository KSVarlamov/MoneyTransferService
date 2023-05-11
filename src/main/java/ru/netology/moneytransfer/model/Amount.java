package ru.netology.moneytransfer.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class Amount {

    @Min(value = 0,  message = "Сумма перевода должна быть больше 0")
    private int value;

    @NotBlank
    @Length(min = 1, max = 3, message = "Название валюты должно быть 1..3 символа")
    private String currency;

}
