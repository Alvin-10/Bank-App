package com.alvin.project;

import com.alvin.project.DTO.TransactionDTO;
import com.alvin.project.Model.Transaction;
import com.alvin.project.Repository.TransactionRepository;
import com.alvin.project.Service.TransactionServiceImpl;
import com.alvin.project.exception.InvalidTransactionException;
import com.alvin.project.exception.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTransaction() {
        // Arrange
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAccountNumber("12345");
        transactionDTO.setAmount(100.0);
        transactionDTO.setType("credit");
        transactionDTO.setTimestamp(String.valueOf(System.currentTimeMillis())); // Use current time in milliseconds

        Transaction transaction = new Transaction();
        transaction.setAccountNumber("12345");
        transaction.setAmount(100.0);
        transaction.setType("credit");
        transaction.setTimestamp("2025-02-03 10:00:00"); // Expected formatted date string

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = transactionService.saveTransaction(transactionDTO);

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getAccountNumber());
        assertEquals(100.0, result.getAmount());
        assertEquals("credit", result.getType());
        assertEquals("2025-02-03 10:00:00", result.getTimestamp());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testSaveTransaction_InvalidAmount() {
        // Arrange
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAccountNumber("12345");
        transactionDTO.setAmount(-100.0);
        transactionDTO.setType("credit");
        transactionDTO.setTimestamp(String.valueOf(System.currentTimeMillis())); // Use current time in milliseconds

        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> transactionService.saveTransaction(transactionDTO));
    }

    @Test
    void testGetTransactionsByAccountNumber() {
        // Arrange
        String accountNumber = "12345";
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(accountNumber);
        transaction.setAmount(100.0);
        transaction.setType("credit");
        transaction.setTimestamp("2025-02-03 10:00:00"); // Expected formatted date string
        transactions.add(transaction);

        when(transactionRepository.findByAccountNumber(accountNumber)).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByAccountNumber(accountNumber);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("12345", result.get(0).getAccountNumber());
    }

    @Test
    void testGetTransactionsByAccountNumber_NotFound() {
        // Arrange
        String accountNumber = "12345";
        when(transactionRepository.findByAccountNumber(accountNumber)).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionsByAccountNumber(accountNumber));
    }
}