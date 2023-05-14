package ru.netology.moneytransfer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.CreditCardNumber;
import ru.netology.moneytransfer.model.Amount;

@Builder
public record CardToCardOperationDTO(
        @NotBlank
        @CreditCardNumber(message = "Недопустимый номер карты отправителя")
        String cardFromNumber,

        @NotBlank
        @Pattern(regexp = "[0-1][0-9]/\\d{2}", message = "Дата должна быть в формате ММ/ГГ")
        String cardFromValidTill,

        @NotBlank
        @Pattern(regexp = "\\d{3,4}", message = "Должно быть 3-4 цифры")
        String cardFromCVV,

        @NotBlank
        @CreditCardNumber(message = "недопустимый номер карты получателя")
        String cardToNumber,

        @Valid
        @NotNull
        Amount amount
) {
}
