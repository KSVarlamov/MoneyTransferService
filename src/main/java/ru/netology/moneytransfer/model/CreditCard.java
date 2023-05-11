package ru.netology.moneytransfer.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CreditCard {

    @CreditCardNumber
    private final String ccNum;

    @NotBlank
    @Length(min = 3, max = 4)
    private final String cvv;

    @NotBlank
    private final String ccTill;

    @NotBlank
    private BigDecimal balance = new BigDecimal(0);

    public void addMoney(BigDecimal amount) {
       this.balance = balance.add(amount);
    }

    public boolean spendMoney(BigDecimal amount) {
        if (amount.compareTo(balance) <= 0) {
            balance = balance.subtract(amount);
            return true;
        }
        return false;
    }

}
