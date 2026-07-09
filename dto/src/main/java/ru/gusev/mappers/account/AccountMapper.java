package ru.gusev.mappers.account;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gusev.account.Account;
import ru.gusev.mappers.operation.OperationMapper;
import ru.gusev.response.account.AccountResponse;
import ru.gusev.response.account.AccountWithOperationsResponse;

@Mapper(componentModel = "spring", uses = OperationMapper.class)
public interface AccountMapper {
    @Mapping(source = "id", target = "accountId")
    @Mapping(source = "owner.id", target = "ownerId")
    AccountResponse toAccountResponse(Account account);

    @Mapping(source = "owner.id", target = "ownerId")
    AccountWithOperationsResponse toResponseWithOperations(Account account);
}
