package com.alvin.project.DTO;

import lombok.Data;

@Data

public class AccountDTO {
	private Long userId;
	private String accountNumber;
	private double amount;
	private String senderAccountNumber;
	private String receiverAccountNumber;
}
