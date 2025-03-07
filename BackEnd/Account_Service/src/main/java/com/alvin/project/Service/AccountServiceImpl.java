package com.alvin.project.Service;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alvin.project.DTO.AccountDTO;
import com.alvin.project.DTO.TransactionDTO;
import com.alvin.project.Model.Account;
import com.alvin.project.Repository.AccountRepository;
import com.alvin.project.exception.AccountNotFoundException;
import com.alvin.project.exception.InsufficientBalanceException;
import com.alvin.project.exception.InvalidInputException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

	// Logger for logging messages
	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	private AccountRepository accountRepository;
	private RestTemplate restTemplate;

	// URLs for User and Transaction Services
	private static final String USER_SERVICE_URL = "http://localhost:8081/users";
	private static final String TRANSACTION_SERVICE_URL = "http://localhost:8083/transactions";

	// Pattern for validating account numbers
	private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{12}$");

	@Override
	public Account sendMoney(AccountDTO accountDTO) {
		logger.info("Initiating money transfer from {} to {}", accountDTO.getSenderAccountNumber(),
				accountDTO.getReceiverAccountNumber());

		// Validate account numbers
		validateAccountNumber(accountDTO.getSenderAccountNumber());
		validateAccountNumber(accountDTO.getReceiverAccountNumber());

		// Retrieve sender and receiver accounts
		Account senderAccount = accountRepository.findByAccountNumber(accountDTO.getSenderAccountNumber());
		Account receiverAccount = accountRepository.findByAccountNumber(accountDTO.getReceiverAccountNumber());

		// Check if accounts exist and if sender has sufficient balance
		if (senderAccount == null) {
			logger.error("Sender account not found: {}", accountDTO.getSenderAccountNumber());
			throw new AccountNotFoundException(
					"Sender account not found with account number: " + accountDTO.getSenderAccountNumber());
		}
		if (receiverAccount == null) {
			logger.error("Receiver account not found: {}", accountDTO.getReceiverAccountNumber());
			throw new AccountNotFoundException(
					"Receiver account not found with account number: " + accountDTO.getReceiverAccountNumber());
		}
		if (senderAccount.getBalance() < accountDTO.getAmount()) {
			logger.error("Insufficient balance in sender account: {}", accountDTO.getSenderAccountNumber());
			throw new InsufficientBalanceException(
					"Insufficient balance in sender account: " + accountDTO.getSenderAccountNumber());
		}

		// Perform the money transfer
		senderAccount.setBalance(senderAccount.getBalance() - accountDTO.getAmount());
		receiverAccount.setBalance(receiverAccount.getBalance() + accountDTO.getAmount());
		accountRepository.save(senderAccount);
		accountRepository.save(receiverAccount);

		// Save transaction for sender
		TransactionDTO senderTransaction = new TransactionDTO(accountDTO.getSenderAccountNumber(),
				accountDTO.getAmount(), "debit", String.valueOf(System.currentTimeMillis()));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<TransactionDTO> senderRequest = new HttpEntity<>(senderTransaction, headers);
		restTemplate.postForObject(TRANSACTION_SERVICE_URL + "/add", senderRequest, Void.class);
		logger.info("Transaction recorded for sender: {}", accountDTO.getSenderAccountNumber());

		// Save transaction for receiver
		TransactionDTO receiverTransaction = new TransactionDTO(accountDTO.getReceiverAccountNumber(),
				accountDTO.getAmount(), "credit", String.valueOf(System.currentTimeMillis()));
		HttpEntity<TransactionDTO> receiverRequest = new HttpEntity<>(receiverTransaction, headers);
		restTemplate.postForObject(TRANSACTION_SERVICE_URL + "/add", receiverRequest, Void.class);
		logger.info("Transaction recorded for receiver: {}", accountDTO.getReceiverAccountNumber());

		return senderAccount;
	}

	@Override
	public double viewBalance(String accountNumber) {
		logger.info("Retrieving balance for account number: {}", accountNumber);

		// Validate account number
		validateAccountNumber(accountNumber);

		// Retrieve account
		Account account = accountRepository.findByAccountNumber(accountNumber);
		if (account == null) {
			logger.error("Account not found: {}", accountNumber);
			throw new AccountNotFoundException("Account not found with account number: " + accountNumber);
		}

		logger.info("Retrieved account: {}", account);
		return account.getBalance();
	}

	@Override
	public Account addMoney(AccountDTO accountDTO) {
		logger.info("Adding money to account number: {}", accountDTO.getSenderAccountNumber());
		logger.info("Amount to add: {}", accountDTO.getAmount());

		// Validate account number
		validateAccountNumber(accountDTO.getSenderAccountNumber());

		// Retrieve account
		Account account = accountRepository.findByAccountNumber(accountDTO.getSenderAccountNumber());
		if (account == null) {
			logger.error("Account not found: {}", accountDTO.getSenderAccountNumber());
			throw new AccountNotFoundException(
					"Account not found with account number: " + accountDTO.getSenderAccountNumber());
		}

		logger.info("Current balance: {}", account.getBalance());
		account.setBalance(account.getBalance() + accountDTO.getAmount());
		Account updatedAccount = accountRepository.save(account);
		logger.info("Updated balance: {}", updatedAccount.getBalance());

		// Save transaction
		TransactionDTO transactionDTO = new TransactionDTO(accountDTO.getSenderAccountNumber(), accountDTO.getAmount(),
				"credit", String.valueOf(System.currentTimeMillis()));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<TransactionDTO> request = new HttpEntity<>(transactionDTO, headers);
		restTemplate.postForObject(TRANSACTION_SERVICE_URL + "/add", request, Void.class);
		logger.info("Transaction recorded for account: {}", accountDTO.getSenderAccountNumber());

		return updatedAccount;
	}

	@Override
	public Account createAccountForUser(AccountDTO accountDTO) {
		logger.info("Creating account for user ID: {}", accountDTO.getUserId());

		// Fetch account details from User Service
		AccountDTO userAccountDTO = getAccountDTOFromUserService(accountDTO.getUserId());
		logger.info("Fetched account number from user service: {}", userAccountDTO.getAccountNumber());

		if (userAccountDTO.getAccountNumber() == null) {
			logger.error("Fetched account number is null for user ID: {}", accountDTO.getUserId());
			throw new AccountNotFoundException("Fetched account number is null for user ID: " + accountDTO.getUserId());
		}

		// Create and save new account
		Account account = new Account();
		account.setAccountNumber(userAccountDTO.getAccountNumber());
		account.setUserId(accountDTO.getUserId());
		account.setBalance(0.0); // Initialize balance to 0

		Account savedAccount = accountRepository.save(account);
		logger.info("Saved account with account number: {}", savedAccount.getAccountNumber());

		return savedAccount;
	}

	private AccountDTO getAccountDTOFromUserService(Long userId) {
		logger.info("Fetching account details from user service for user ID: {}", userId);

		// Send GET request to User Service
		ResponseEntity<AccountDTO> response = restTemplate.getForEntity(USER_SERVICE_URL + "/accountNumber/" + userId,
				AccountDTO.class);
		AccountDTO accountDTO = response.getBody();
		logger.info("Response from user service: {}", accountDTO);

		return accountDTO;
	}

	private void validateAccountNumber(String accountNumber) {
		// Validate account number format
		if (!ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
			logger.error("Invalid account number: {}", accountNumber);
			throw new InvalidInputException("Invalid account number: " + accountNumber);
		}
	}
}