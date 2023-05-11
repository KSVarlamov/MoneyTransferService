package ru.netology.moneytransfer.repository;

import org.springframework.stereotype.Repository;
import ru.netology.moneytransfer.model.CreditCard;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCCardRepository implements CCardRepository {

    private static final ConcurrentHashMap<String, CreditCard> cards = new ConcurrentHashMap<>();

    @Override
    public CreditCard addCC(CreditCard cc) {
        return cards.put(cc.getCcNum(), cc);
    }

    @Override
    public boolean delCC(CreditCard cc) {
        return cards.remove(cc.getCcNum(), cc);
    }

    @Override
    public Optional<CreditCard> getCardByNumber(String ccNum) {
        return Optional.ofNullable(cards.get(ccNum));
    }

    @Override
    public void deleteAll() {
        cards.clear();
    }
}
