package com.alvin.project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
	private String accountNumber;
	private double amount;
	private String type; // "credit" or "debit"
	private String timestamp;

}