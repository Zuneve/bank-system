package ru.gusev.request.rate;

import java.util.UUID;

public record RateRequestMessage(
        String currency
) {
}
