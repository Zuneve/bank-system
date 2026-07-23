package ru.gusev.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gusev.kafka.RateProvider;
import ru.gusev.operation.Operation;
import ru.gusev.operation.OperationService;
import ru.gusev.operation.OperationType;
import ru.gusev.response.operation.CurrencyOperationResponse;
import ru.gusev.update.RateUpdateMessage;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrencyOperationService {
    private final OperationService operationService;
    private final RateProvider rateProvider;
    private final CurrencyConverter currencyConverter;

    public List<CurrencyOperationResponse> getOperations(
            UUID accountId,
            OperationType operationType,
            String currency) {
        RateUpdateMessage rate = rateProvider.getRate(currency);
        return operationService.getOperationsByFilters(accountId, operationType)
                .stream()
                .map(operation -> toResponse(operation, rate))
                .toList();
    }

    private CurrencyOperationResponse toResponse(
            Operation operation,
            RateUpdateMessage rate) {
        return new CurrencyOperationResponse(
                operation.getId(),
                operation.getType(),
                operation.getAmount(),
                rate.currency(),
                currencyConverter.fromRub(operation.getAmount(), rate),
                rate.rateToRub(),
                rate.timestamp(),
                operation.getCreatedAt(),
                operation.getAccount().getId()
        );
    }
}
