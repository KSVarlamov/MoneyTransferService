package ru.netology.moneytransfer.repository;

import org.springframework.stereotype.Repository;
import ru.netology.moneytransfer.model.CreditCard;

import java.math.BigDecimal;
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

    //TODO удалить

    static {
        CreditCard cc = new CreditCard("4960144072893312", "157", "11/23", new BigDecimal(1000));
        cards.put(cc.getCcNum(), cc);
        cc = new CreditCard("4960149153260042", "487", "01/24", new BigDecimal(0));
        cards.put(cc.getCcNum(), cc);
        cc = new CreditCard("4960147364985126", "976", "01/24", new BigDecimal(500));
        cards.put(cc.getCcNum(), cc);
    }
}
