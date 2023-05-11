package ru.netology.moneytransfer.repository;

import org.springframework.stereotype.Repository;
import ru.netology.moneytransfer.model.CreditCard;

import java.util.Optional;

@Repository
public interface CCardRepository {
    CreditCard addCC(CreditCard cc);
    boolean delCC(CreditCard cc);
    Optional<CreditCard> getCardByNumber(String ccNum);

    void deleteAll();

}
