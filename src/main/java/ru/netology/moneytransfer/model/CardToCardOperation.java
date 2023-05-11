package ru.netology.moneytransfer.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Random;

@Data
@Validated
public class CardToCardOperation {
    @Min(1)
    private final int id;

    @NotNull
    private CreditCard ccFrom;

    @NotNull
    private CreditCard ccTo;

    @NotNull
    private String reason = "";

    @NotNull
    private String code;

    @NotNull
    Amount amount;

    @Setter(AccessLevel.NONE)
    private final LocalDate created;

    @Setter(AccessLevel.NONE)
    private int codeChecksLast = 3;

    private Status status = Status.NEW;

    public CardToCardOperation(int id) {
        this.id = id;
        this.created = LocalDate.now();
        this.status = Status.NEW;
        Random rnd = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            s.append(String.valueOf(rnd.nextInt(10)));
        }
        this.code = s.toString();
    }

    public boolean checkCode (String code) {
        if (!status.equals(Status.WAITING_FOR_CONFIRM)) {
            return false;
        }
        if (codeChecksLast > 1) {
            codeChecksLast--;
            return this.code.equals(code);
        }
        status = Status.FAILED;
        reason = "incorrect code 3 times";
        return false;
    }

    public enum Status {
        NEW,
        WAITING_FOR_CONFIRM,
        DONE,
        FAILED
    }

}
