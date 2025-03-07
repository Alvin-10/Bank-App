package com.alvin.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvin.project.DTO.AccountDTO;
import com.alvin.project.Model.Account;
import com.alvin.project.Service.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController {
	@Autowired
	private AccountService accountService;

	/**
	 * Creates a new account for a user.
	 * 
	 * @param accountDTO the account data transfer object containing user ID and
	 *                   account details
	 * @return the created account
	 */
	@PostMapping("/create")
	public ResponseEntity<Account> createAccount(@RequestBody AccountDTO accountDTO) {
		Account account = accountService.createAccountForUser(accountDTO);
		return ResponseEntity.ok(account);
	}

	/**
	 * Transfers money from one account to another.
	 * 
	 * @param accountDTO the account data transfer object containing sender and
	 *                   receiver account details and the amount to transfer
	 * @return the updated sender account after the transfer
	 */
	@PostMapping("/send")
	public ResponseEntity<Account> sendMoney(@RequestBody AccountDTO accountDTO) {
		Account account = accountService.sendMoney(accountDTO);
		return ResponseEntity.ok(account);
	}

	/**
	 * Adds money to an account.
	 * 
	 * @param accountDTO the account data transfer object containing the account
	 *                   number and the amount to add
	 * @return the updated account after adding the money
	 */
	@PostMapping("/add")
	public ResponseEntity<Account> addMoney(@RequestBody AccountDTO accountDTO) {
		Account account = accountService.addMoney(accountDTO);
		return ResponseEntity.ok(account);
	}

	/**
	 * Retrieves the balance of an account.
	 * 
	 * @param accountNumber the account number to retrieve the balance for
	 * @return the balance of the account
	 */
	@GetMapping("/balance/{accountNumber}")
	public ResponseEntity<Double> viewBalance(@PathVariable String accountNumber) {
		double balance = accountService.viewBalance(accountNumber);
		return ResponseEntity.ok(balance);
	}
}