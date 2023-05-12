package ru.netology.moneytransfer.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Validated
public class CardToCardOperation {

    private final int id;

    @Setter(AccessLevel.NONE)
    private final LocalDate created;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    @NotNull
    BigDecimal commission;

    @NotNull
    private String ccFrom;

    @NotNull
    private String ccTo;

    @NotNull
    private String reason = "";

    @NotNull
    private String code = "0000";

    @Setter(AccessLevel.NONE)
    private int codeChecksLast = 3;

    private Status status;

    public CardToCardOperation(int id) {
        this.id = id;
        this.created = LocalDate.now();
        this.status = Status.NEW;
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
