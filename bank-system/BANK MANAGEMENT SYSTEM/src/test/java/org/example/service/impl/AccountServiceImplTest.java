package org.example.service.impl;

import org.example.dto.AccountDto;
import org.example.entity.Account;
import org.example.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AccountServiceImplTest {

    private AccountRepository accountRepository;
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    void depositAddsAmountToAccountBalance() {
        Account account = new Account(1L, "Test Account", 100.0);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountDto result = accountService.deposit(1L, 25.0);

        assertEquals(125.0, result.getBalance(), 0.000001);
        verify(accountRepository).save(account);
    }

    @Test
    void depositRejectsNonPositiveAmountsBeforeLoadingAccount() {
        assertThrows(IllegalArgumentException.class, () -> accountService.deposit(1L, 0.0));

        verifyNoInteractions(accountRepository);
    }

    @Test
    void withdrawRejectsNegativeAmountsBeforeLoadingAccount() {
        assertThrows(IllegalArgumentException.class, () -> accountService.withdraw(1L, -10.0));

        verifyNoInteractions(accountRepository);
    }
}
