package com.alvin.project.Service;

import com.alvin.project.DTO.AccountDTO;
import com.alvin.project.Model.Account;

public interface AccountService {
    Account sendMoney(AccountDTO accountDTO);
    double viewBalance(String accountNumber);
    Account addMoney(AccountDTO accountDTO);
    Account createAccountForUser(AccountDTO accountDTO);
}