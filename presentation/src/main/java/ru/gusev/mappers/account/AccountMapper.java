package ru.gusev.mappers.account;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gusev.account.Account;
import ru.gusev.response.account.AccountResponse;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "id", target = "accountId")
    @Mapping(source = "owner.id", target = "ownerId")
    AccountResponse toAccountResponse(Account account);
}
