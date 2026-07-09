package ru.gusev.mappers.operation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gusev.response.operation.OperationResponse;
import ru.gusev.operation.Operation;

@Mapper(componentModel = "spring")
public interface OperationMapper {
    @Mapping(source = "account.id", target = "accountId")
    OperationResponse toOperationResponse(Operation operation);
}
