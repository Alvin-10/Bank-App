package com.alvin.project.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvin.project.DTO.TransactionDTO;
import com.alvin.project.Model.Transaction;
import com.alvin.project.Service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	@Autowired
	private TransactionService transactionService;

	/**
	 * Adds a new transaction.
	 * 
	 * @param transactionDTO the transaction data transfer object containing
	 *                       transaction details
	 * @return the saved transaction
	 */
	@PostMapping("/add")
	public ResponseEntity<Transaction> addTransaction(@RequestBody TransactionDTO transactionDTO) {
		Transaction transaction = transactionService.saveTransaction(transactionDTO);
		return ResponseEntity.ok(transaction);
	}

	/**
	 * Retrieves the transaction history for a specific account.
	 * 
	 * @param accountNumber the account number to retrieve the transaction history
	 *                      for
	 * @return the list of transactions for the specified account
	 */
	@GetMapping("/history/{accountNumber}")
	public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable String accountNumber) {
		List<Transaction> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
		return ResponseEntity.ok(transactions);
	}
}