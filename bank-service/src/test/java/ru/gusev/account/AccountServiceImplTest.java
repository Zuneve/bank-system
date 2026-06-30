package ru.gusev.account;

import ru.gusev.operation.Operation;
import ru.gusev.operation.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.gusev.operation.OperationHistoryRepository;
import ru.gusev.user.UserRepository;
import ru.gusev.user.User;
import ru.gusev.user.info.HairColor;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceImplTest {
    private AccountRepository accountRepository;
    private OperationHistoryRepository operationHistoryRepository;
    private UserRepository userRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        operationHistoryRepository = mock(OperationHistoryRepository.class);
        userRepository = mock(UserRepository.class);
        accountService = new AccountServiceImpl(accountRepository, operationHistoryRepository, userRepository);
    }

    @Test
    void withdrawWithEnoughBalanceSavesUpdatedAccountAndHistory() {
        Account account = accountWithBalance(new User("login", "Name", 20, true, HairColor.Black), "100.00");
        when(accountRepository.getAccountById(account.getId())).thenReturn(Optional.of(account));

        accountService.withdraw(account.getId(), new BigDecimal("35.50"));

        assertAmountEquals("64.50", account.getBalance());
        verify(accountRepository).save(account);

        ArgumentCaptor<Operation> operationCaptor = ArgumentCaptor.forClass(Operation.class);
        verify(operationHistoryRepository).addOperation(eq(account.getId()), operationCaptor.capture());
        assertEquals(OperationType.WITHDRAW, operationCaptor.getValue().getType());
        assertAmountEquals("35.50", operationCaptor.getValue().getAmount());
    }

    @Test
    void withdrawWithNotEnoughBalanceThrowsDomainError() {
        Account account = accountWithBalance(new User("login", "Name", 20, true, HairColor.Black), "10.00");
        when(accountRepository.getAccountById(account.getId())).thenReturn(Optional.of(account));

        assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(account.getId(), new BigDecimal("20.00")));

        assertAmountEquals("10.00", account.getBalance());
        verify(accountRepository, never()).save(any());
        verify(operationHistoryRepository, never()).addOperation(any(), any());
    }

    @Test
    void depositSavesUpdatedAccountAndHistory() {
        Account account = accountWithBalance(new User("login", "Name", 20, true, HairColor.Black), "10.00");
        when(accountRepository.getAccountById(account.getId())).thenReturn(Optional.of(account));

        accountService.deposit(account.getId(), new BigDecimal("25.25"));

        assertAmountEquals("35.25", account.getBalance());
        verify(accountRepository).save(account);

        ArgumentCaptor<Operation> operationCaptor = ArgumentCaptor.forClass(Operation.class);
        verify(operationHistoryRepository).addOperation(eq(account.getId()), operationCaptor.capture());
        assertEquals(OperationType.DEPOSIT, operationCaptor.getValue().getType());
        assertAmountEquals("25.25", operationCaptor.getValue().getAmount());
    }

    @Test
    void transferBetweenOwnAccountsHasNoCommissionAndSavesBothSides() {
        User owner = new User("owner", "Owner", 30, true, HairColor.White);
        Account source = accountWithBalance(owner, "100.00");
        Account target = accountWithBalance(owner, "5.00");
        mockAccounts(source, target);

        accountService.transfer(source.getId(), target.getId(), new BigDecimal("40.00"));

        assertAmountEquals("60.00", source.getBalance());
        assertAmountEquals("45.00", target.getBalance());
        verify(accountRepository).save(source);
        verify(accountRepository).save(target);
        verifyTransferHistory(source, "40.00", target, "40.00");
    }

    @Test
    void transferToFriendWithdrawsAmountPlusThreePercentCommission() {
        User owner = new User("owner", "Owner", 30, true, HairColor.White);
        User friend = new User("friend", "Friend", 31, false, HairColor.Black);
        owner.getFriends().add(friend);
        Account source = accountWithBalance(owner, "100.00");
        Account target = accountWithBalance(friend, "5.00");
        mockAccounts(source, target);

        accountService.transfer(source.getId(), target.getId(), new BigDecimal("40.00"));

        assertAmountEquals("58.8000", source.getBalance());
        assertAmountEquals("45.00", target.getBalance());
        verifyTransferHistory(source, "41.2000", target, "40.00");
    }

    @Test
    void transferToOtherUserWithdrawsAmountPlusTenPercentCommission() {
        User owner = new User("owner", "Owner", 30, true, HairColor.White);
        User other = new User("other", "Other", 31, false, HairColor.Black);
        Account source = accountWithBalance(owner, "100.00");
        Account target = accountWithBalance(other, "5.00");
        mockAccounts(source, target);

        accountService.transfer(source.getId(), target.getId(), new BigDecimal("40.00"));

        assertAmountEquals("56.0000", source.getBalance());
        assertAmountEquals("45.00", target.getBalance());
        verifyTransferHistory(source, "44.0000", target, "40.00");
    }

    private Account accountWithBalance(User owner, String balance) {
        Account account = new Account(owner);
        account.setBalance(new BigDecimal(balance));
        return account;
    }

    private void mockAccounts(Account source, Account target) {
        when(accountRepository.getAccountById(source.getId())).thenReturn(Optional.of(source));
        when(accountRepository.getAccountById(target.getId())).thenReturn(Optional.of(target));
    }

    private void verifyTransferHistory(
            Account source,
            String expectedSourceOperationAmount,
            Account target,
            String expectedTargetOperationAmount
    ) {
        ArgumentCaptor<Operation> sourceOperationCaptor = ArgumentCaptor.forClass(Operation.class);
        ArgumentCaptor<Operation> targetOperationCaptor = ArgumentCaptor.forClass(Operation.class);

        verify(operationHistoryRepository).addOperation(eq(source.getId()), sourceOperationCaptor.capture());
        verify(operationHistoryRepository).addOperation(eq(target.getId()), targetOperationCaptor.capture());
        verify(accountRepository, times(2)).save(any(Account.class));

        assertEquals(OperationType.TRANSFER_OUT, sourceOperationCaptor.getValue().getType());
        assertAmountEquals(expectedSourceOperationAmount, sourceOperationCaptor.getValue().getAmount());
        assertEquals(OperationType.TRANSFER_IN, targetOperationCaptor.getValue().getType());
        assertAmountEquals(expectedTargetOperationAmount, targetOperationCaptor.getValue().getAmount());
    }

    private void assertAmountEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
