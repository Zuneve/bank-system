package ru.gusev.currency;

import org.junit.jupiter.api.Test;
import ru.gusev.account.Account;
import ru.gusev.kafka.RateProvider;
import ru.gusev.operation.Operation;
import ru.gusev.operation.OperationService;
import ru.gusev.operation.OperationType;
import ru.gusev.response.operation.CurrencyOperationResponse;
import ru.gusev.update.RateUpdateMessage;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyOperationServiceTest {
    private final OperationService operationService =
            mock(OperationService.class);
    private final RateProvider rateProvider = mock(RateProvider.class);
    private final CurrencyOperationService currencyOperationService =
            new CurrencyOperationService(
                    operationService,
                    rateProvider,
                    new CurrencyConverter()
            );

    @Test
    void operationsShouldBeLoadedWithFilters() {
        UUID accountId = UUID.randomUUID();
        OperationType operationType = OperationType.DEPOSIT;
        prepareRate();
        when(operationService.getOperationsByFilters(
                accountId,
                operationType
        )).thenReturn(List.of());

        currencyOperationService.getOperations(
                accountId,
                operationType,
                "USD"
        );

        verify(operationService).getOperationsByFilters(
                accountId,
                operationType
        );
    }

    @Test
    void rateShouldBeRequestedOnceForAllOperations() {
        UUID accountId = UUID.randomUUID();
        prepareRate();
        when(operationService.getOperationsByFilters(accountId, null))
                .thenReturn(createOperations());

        currencyOperationService.getOperations(accountId, null, "USD");

        verify(rateProvider, times(1)).getRate("USD");
    }

    @Test
    void operationAmountsShouldBeConvertedCorrectly() {
        UUID accountId = UUID.randomUUID();
        prepareRate();
        when(operationService.getOperationsByFilters(accountId, null))
                .thenReturn(createOperations());

        List<CurrencyOperationResponse> responses =
                currencyOperationService.getOperations(
                        accountId,
                        null,
                        "USD"
                );

        assertEquals(new BigDecimal("9150.00"), responses.get(0).amountRub());
        assertEquals(new BigDecimal("100.00"), responses.get(0).amount());
        assertEquals(new BigDecimal("18300.00"), responses.get(1).amountRub());
        assertEquals(new BigDecimal("200.00"), responses.get(1).amount());
    }

    private void prepareRate() {
        when(rateProvider.getRate("USD")).thenReturn(new RateUpdateMessage(
                "USD",
                new BigDecimal("91.50"),
                Instant.parse("2026-07-24T12:00:00Z")
        ));
    }

    private List<Operation> createOperations() {
        Account account = new Account(null);
        return List.of(
                new Operation(
                        OperationType.DEPOSIT,
                        new BigDecimal("9150.00"),
                        account
                ),
                new Operation(
                        OperationType.WITHDRAW,
                        new BigDecimal("18300.00"),
                        account
                )
        );
    }
}
