package ru.gusev.controllers.operation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gusev.response.operation.OperationResponse;
import ru.gusev.mappers.operation.OperationMapper;
import ru.gusev.operation.OperationService;
import ru.gusev.operation.OperationType;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;
    private final OperationMapper operationMapper;

    @Operation(summary = "Get operations filtered by account and operation type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operations returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account identifier or operation type"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<OperationResponse> getOperations(
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) OperationType operationType) {
        return operationService.getOperationsByFilters(accountId, operationType)
                .stream()
                .map(operationMapper::toOperationResponse)
                .toList();
    }
}
