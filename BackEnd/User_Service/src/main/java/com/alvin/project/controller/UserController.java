package com.alvin.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvin.project.DTO.AccountDTO;
import com.alvin.project.DTO.UserDTO;
import com.alvin.project.Model.User;
import com.alvin.project.Service.UserService;
import com.alvin.project.exception.UserNotFoundException;

@RestController
@RequestMapping("/users")

public class UserController {
	@Autowired
	private UserService userService;

	/**
	 * Registers a new user.
	 * 
	 * @param userDTO the user data transfer object containing user details
	 * @return the registered user
	 */
	@PostMapping("/register")
	public ResponseEntity<User> registerUser(@RequestBody UserDTO userDTO) {
		User user = userService.registerUser(userDTO);
		return ResponseEntity.ok(user);
	}

	/**  
	 * Retrieves a user by their ID.
	 * 
	 * @param id the ID of the user
	 * @return the user with the specified ID
	 * @throws UserNotFoundException if the user ID is invalid or the user is not
	 *                               found
	 */
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable String id) {
		try {
			Long userId = Long.parseLong(id);
			User user = userService.getUserById(userId)
					.orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
			return ResponseEntity.ok(user);
		} catch (NumberFormatException e) {
			throw new UserNotFoundException("Invalid user id: " + id);
		}
	}

	/**
	 * Retrieves the account details for a user by their user ID.
	 * 
	 * @param userId the ID of the user
	 * @return the account details of the user
	 */
	@GetMapping("/accountNumber/{userId}")
	public ResponseEntity<AccountDTO> getAccountDTO(@PathVariable Long userId) {
		AccountDTO accountDTO = userService.getAccountDTOByUserId(userId);
		return ResponseEntity.ok(accountDTO);
	}

	/**
	 * Updates the details of an existing user.
	 * 
	 * @param id      the ID of the user to update
	 * @param userDTO the user data transfer object containing updated user details
	 * @return the updated user
	 * @throws UserNotFoundException if the user ID is invalid or the user is not
	 *                               found
	 */
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UserDTO userDTO) {
		try {
			Long userId = Long.parseLong(id);
			User updatedUser = userService.updateUser(userId, userDTO);
			return ResponseEntity.ok(updatedUser);
		} catch (NumberFormatException e) {
			throw new UserNotFoundException("Invalid user id: " + id);
		}
	}
}