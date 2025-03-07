package com.alvin.project;

import com.alvin.project.DTO.AccountDTO;
import com.alvin.project.Model.Account;
import com.alvin.project.Repository.AccountRepository;
import com.alvin.project.Service.AccountServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMoney() {
        // Arrange
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setSenderAccountNumber("123456789012");
        accountDTO.setReceiverAccountNumber("678901234567");
        accountDTO.setAmount(100.0);

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("123456789012");
        senderAccount.setBalance(200.0);

        Account receiverAccount = new Account();
        receiverAccount.setAccountNumber("678901234567");
        receiverAccount.setBalance(50.0);

        when(accountRepository.findByAccountNumber("123456789012")).thenReturn(senderAccount);
        when(accountRepository.findByAccountNumber("678901234567")).thenReturn(receiverAccount);

        // Act
        Account result = accountService.sendMoney(accountDTO);

        // Assert
        assertEquals(100.0, senderAccount.getBalance());
        assertEquals(150.0, receiverAccount.getBalance());
        verify(accountRepository, times(1)).save(senderAccount);
        verify(accountRepository, times(1)).save(receiverAccount);
    }

    @Test
    void testViewBalance() {
        // Arrange
        String accountNumber = "123456789012";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(200.0);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(account);

        // Act
        double balance = accountService.viewBalance(accountNumber);

        // Assert
        assertEquals(200.0, balance);
    }

    @Test
    void testAddMoney() {
        // Arrange
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setSenderAccountNumber("123456789012");
        accountDTO.setAmount(100.0);

        Account account = new Account();
        account.setAccountNumber("123456789012");
        account.setBalance(200.0);

        when(accountRepository.findByAccountNumber("123456789012")).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        // Act
        Account result = accountService.addMoney(accountDTO);

        // Assert
        assertEquals(300.0, result.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testCreateAccountForUser() {
        // Arrange
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setSenderAccountNumber("123456789012");
        accountDTO.setUserId(1L);

        Account account = new Account();
        account.setAccountNumber("123456789012");
        account.setUserId(1L);
        account.setBalance(0.0);

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Mock the response from the User Service
        AccountDTO userAccountDTO = new AccountDTO();
        userAccountDTO.setAccountNumber("123456789012");
        when(restTemplate.getForEntity(anyString(), eq(AccountDTO.class))).thenReturn(new ResponseEntity<>(userAccountDTO, HttpStatus.OK));

        // Act
        Account result = accountService.createAccountForUser(accountDTO);

        // Assert
        assertEquals("123456789012", result.getAccountNumber());
        assertEquals(1L, result.getUserId());
        assertEquals(0.0, result.getBalance());
    }
}