package ru.gusev.currency;

import org.springframework.stereotype.Component;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyConverter {

    public BigDecimal fromRub(BigDecimal amountRub, RateUpdateMessage rate) {
        if ("RUB".equals(rate.currency())) {
            return amountRub.setScale(2, RoundingMode.HALF_UP);
        }
        return amountRub.divide(rate.rateToRub(), 2, RoundingMode.HALF_UP);
    }
}
