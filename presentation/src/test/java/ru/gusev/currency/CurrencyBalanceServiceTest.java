package ru.gusev.currency;

import org.junit.jupiter.api.Test;
import ru.gusev.account.AccountService;
import ru.gusev.kafka.RateProvider;
import ru.gusev.response.account.CurrencyBalanceResponse;
import ru.gusev.services.ClientService;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyBalanceServiceTest {
    private final AccountService accountService = mock(AccountService.class);
    private final RateProvider rateProvider = mock(RateProvider.class);
    private final ClientService clientService = mock(ClientService.class);
    private final CurrencyBalanceService currencyBalanceService =
            new CurrencyBalanceService(
                    accountService,
                    rateProvider,
                    new CurrencyConverter(),
                    clientService
            );

    @Test
    void balanceShouldBeReceivedFromAccountService() {
        UUID accountId = UUID.randomUUID();
        prepareBalanceAndRate(accountId);

        currencyBalanceService.getBalance(accountId, "USD");

        verify(accountService).getBalanceById(accountId);
    }

    @Test
    void rateShouldBeReceivedFromRateProvider() {
        UUID accountId = UUID.randomUUID();
        prepareBalanceAndRate(accountId);

        currencyBalanceService.getBalance(accountId, "USD");

        verify(rateProvider).getRate("USD");
    }

    @Test
    void balanceShouldBeConvertedCorrectly() {
        UUID accountId = UUID.randomUUID();
        prepareBalanceAndRate(accountId);

        CurrencyBalanceResponse response =
                currencyBalanceService.getBalance(accountId, "USD");

        assertEquals(new BigDecimal("100.00"), response.balance());
    }

    @Test
    void originalRubBalanceShouldNotBeChanged() {
        UUID accountId = UUID.randomUUID();
        BigDecimal balanceRub = new BigDecimal("9150.00");
        prepareBalanceAndRate(accountId, balanceRub);

        CurrencyBalanceResponse response =
                currencyBalanceService.getBalance(accountId, "USD");

        assertEquals(new BigDecimal("9150.00"), balanceRub);
        assertEquals(balanceRub, response.balanceRub());
    }

    private void prepareBalanceAndRate(UUID accountId) {
        prepareBalanceAndRate(accountId, new BigDecimal("9150.00"));
    }

    private void prepareBalanceAndRate(
            UUID accountId,
            BigDecimal balanceRub) {
        when(accountService.getBalanceById(accountId)).thenReturn(balanceRub);
        when(rateProvider.getRate("USD")).thenReturn(new RateUpdateMessage(
                "USD",
                new BigDecimal("91.50"),
                Instant.parse("2026-07-24T12:00:00Z")
        ));
    }
}
