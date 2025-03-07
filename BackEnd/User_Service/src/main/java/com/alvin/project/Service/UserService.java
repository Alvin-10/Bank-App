 package com.alvin.project.Service;

import com.alvin.project.DTO.AccountDTO;
import com.alvin.project.DTO.UserDTO;
import com.alvin.project.Model.User;

import java.util.Optional;

public interface UserService {
    User registerUser(UserDTO userDTO);
    Optional<User> getUserById(Long id);
    User updateUser(Long id, UserDTO userDTO);
    AccountDTO getAccountDTOByUserId(Long userId);
}
