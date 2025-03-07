package com.alvin.project.Service;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alvin.project.DTO.AccountDTO;
import com.alvin.project.DTO.UserDTO;
import com.alvin.project.Model.User;
import com.alvin.project.Repository.UserRepository;
import com.alvin.project.exception.UserNotFoundException;

import lombok.AllArgsConstructor;

import com.alvin.project.exception.InvalidInputException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    
    private UserRepository userRepository;

   
    private RestTemplate restTemplate;

    // URL for the Account Service
    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8082/accounts/create";

    // Patterns for validating phone numbers and email addresses
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    public User registerUser(UserDTO userDTO) {
        // Validate user input
        validateUserInput(userDTO);

        // Create a new User object and set its properties
        User user = new User();
        user.setName(userDTO.getName());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());

        // Generate a unique account number for the user
        String generatedAccountNumber = generateUniqueAccountNumber();
        user.setAccountNumber(generatedAccountNumber);
        logger.info("Generated account number for user: {}", generatedAccountNumber);

        // Save the user to the repository
        User savedUser = userRepository.save(user);

        // Create an account for the user in the Account Service
        createAccountForUser(savedUser);

        return savedUser;
    }

    private void createAccountForUser(User user) {
        // Create an AccountDTO object with the user's details
        AccountDTO accountDTO = new AccountDTO(user.getId(), user.getAccountNumber(), 0.0); // Initialize amount to 0.0

        // Send a POST request to the Account Service to create the account
        restTemplate.postForObject(ACCOUNT_SERVICE_URL, accountDTO, Void.class);
        logger.info("Account created for user: {}", user.getId());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user with id: {}", id);

        // Find the user by ID, or throw an exception if not found
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id)));
    }

    @Override
    public User updateUser(Long id, UserDTO userDTO) {
        logger.info("Updating user with id: {}", id);

        // Validate user input
        validateUserInput(userDTO);

        // Find the user by ID, or throw an exception if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Update the user's properties
        user.setName(userDTO.getName());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());

        // Save the updated user to the repository
        return userRepository.save(user);
    }

    @Override
    public AccountDTO getAccountDTOByUserId(Long userId) {
        logger.info("Fetching account details for user id: {}", userId);

        // Find the user by ID, or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Return an AccountDTO object with the user's account details
        return new AccountDTO(user.getId(), user.getAccountNumber(), 0.0); // Initialize amount to 0.0
    }

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();

        // Generate a 12-digit random account number
        for (int i = 0; i < 12; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }

    private void validateUserInput(UserDTO userDTO) {
        // Validate the phone number
        if (!PHONE_PATTERN.matcher(userDTO.getMobileNumber()).matches()) {
            logger.error("Invalid phone number: {}", userDTO.getMobileNumber());
            throw new InvalidInputException("Invalid phone number: " + userDTO.getMobileNumber());
        }

        // Validate the email address
        if (!EMAIL_PATTERN.matcher(userDTO.getEmail()).matches()) {
            logger.error("Invalid email address: {}", userDTO.getEmail());
            throw new InvalidInputException("Invalid email address: " + userDTO.getEmail());
        }
    }
}