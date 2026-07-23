package ru.gusev.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gusev.account.AccountService;
import ru.gusev.kafka.RateProvider;
import ru.gusev.response.account.CurrencyBalanceResponse;
import ru.gusev.services.ClientService;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrencyBalanceService {
    private final AccountService accountService;
    private final RateProvider rateProvider;
    private final CurrencyConverter currencyConverter;
    private final ClientService clientService;

    public CurrencyBalanceResponse getBalance(UUID accountId, String currency) {
        // TODO check account id
        BigDecimal balanceRub = accountService.getBalanceById(accountId);
        RateUpdateMessage rate = rateProvider.getRate(currency);
        BigDecimal convertedBalance = currencyConverter.fromRub(balanceRub, rate);

        return new CurrencyBalanceResponse(
                accountId,
                balanceRub,
                rate.currency(),
                convertedBalance,
                rate.rateToRub(),
                rate.timestamp()
        );
    }
}
