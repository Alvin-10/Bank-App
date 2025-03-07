package com.alvin.project.Service;

import com.alvin.project.DTO.TransactionDTO;
import com.alvin.project.Model.Transaction;
import java.util.List;

public interface TransactionService {
    Transaction saveTransaction(TransactionDTO transactionDTO);
    List<Transaction> getTransactionsByAccountNumber(String accountNumber);
}