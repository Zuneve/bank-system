package ru.gusev.response.account;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.gusev.response.operation.OperationResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "Account data with operation history")
public record AccountWithOperationsResponse(

        @Schema(description = "Account identifier")
        UUID id,

        @Schema(description = "Current account balance")
        BigDecimal balance,

        @Schema(description = "Account owner identifier")
        UUID ownerId,

        @Schema(description = "Account operation history")
        List<OperationResponse> operations

) {
}
