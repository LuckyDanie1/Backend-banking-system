package org.example.service.impl;

import org.example.dto.AccountDto;
import org.example.entity.Account;
import org.example.exception.AccountException;
import org.example.mapper.AccountMapper;
import org.example.repository.AccountRepository;
import org.example.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        if (accountDto == null) {
            throw new IllegalArgumentException("Account details are required");
        }
        if (accountDto.getAccountHolderName() == null || accountDto.getAccountHolderName().isBlank()) {
            throw new IllegalArgumentException("Account holder name is required");
        }
        if (!Double.isFinite(accountDto.getBalance()) || accountDto.getBalance() < 0) {
            throw new IllegalArgumentException("Opening balance must be a non-negative finite amount");
        }

        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exist"));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        validateTransactionAmount(amount);
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exist"));

        double total = account.getBalance() + amount;
        if (!Double.isFinite(total)) {
            throw new IllegalArgumentException("The resulting balance is outside the supported range");
        }
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        validateTransactionAmount(amount);
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exist"));

        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        double total = account.getBalance() - amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    private void validateTransactionAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be a positive finite amount");
        }
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(AccountMapper::mapToAccountDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exist"));

        accountRepository.deleteById(id);
    }
}
