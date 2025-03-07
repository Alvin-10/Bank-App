package com.alvin.project.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alvin.project.DTO.TransactionDTO;
import com.alvin.project.Model.Transaction;
import com.alvin.project.Repository.TransactionRepository;
import com.alvin.project.exception.InvalidTransactionException;
import com.alvin.project.exception.TransactionNotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	// Logger for logging messages
	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	private TransactionRepository transactionRepository;

	@Override
	public Transaction saveTransaction(TransactionDTO transactionDTO) {
		logger.info("Saving transaction for account number: {}", transactionDTO.getAccountNumber());

		// Validate transaction amount
		if (transactionDTO.getAmount() <= 0) {
			logger.error("Invalid transaction amount: {}", transactionDTO.getAmount());
			throw new InvalidTransactionException("Transaction amount must be greater than zero");
		}

		// Convert the timestamp to a formatted date string
		long timestamp = Long.parseLong(transactionDTO.getTimestamp());
		Date date = new Date(timestamp);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = sdf.format(date);

		// Create and save the transaction
		Transaction transaction = new Transaction();
		transaction.setAccountNumber(transactionDTO.getAccountNumber());
		transaction.setAmount(transactionDTO.getAmount());
		transaction.setType(transactionDTO.getType());
		transaction.setTimestamp(formattedDate); // Set the formatted date string
		Transaction savedTransaction = transactionRepository.save(transaction);

		logger.info("Transaction saved with ID: {}", savedTransaction.getId());
		return savedTransaction;
	}

	@Override
	public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
		logger.info("Fetching transactions for account number: {}", accountNumber);

		// Retrieve transactions by account number
		List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber);
		if (transactions.isEmpty()) {
			logger.error("No transactions found for account number: {}", accountNumber);
			throw new TransactionNotFoundException("No transactions found for account number: " + accountNumber);
		}

		logger.info("Retrieved {} transactions for account number: {}", transactions.size(), accountNumber);
		return transactions;
	}
}