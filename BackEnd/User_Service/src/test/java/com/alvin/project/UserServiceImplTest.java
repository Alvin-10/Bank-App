package com.alvin.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.alvin.project.DTO.AccountDTO;
import com.alvin.project.DTO.UserDTO;
import com.alvin.project.Model.User;
import com.alvin.project.Repository.UserRepository;
import com.alvin.project.Service.UserServiceImpl;

public class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testRegisterUser() {
		// Arrange
		UserDTO userDTO = new UserDTO();
		userDTO.setName("Alvin Lnu");
		userDTO.setMobileNumber("1234567890");
		userDTO.setEmail("alvin.lnu@example.com");
		userDTO.setAge(30);

		User user = new User();
		user.setId(1L);
		user.setName("Alvin Lnu");
		user.setMobileNumber("1234567890");
		user.setEmail("alvin.lnu@example.com");
		user.setAge(30);
		user.setAccountNumber("123456789012");

		when(userRepository.save(any(User.class))).thenReturn(user);

		// Act
		User result = userService.registerUser(userDTO);

		// Assert
		assertNotNull(result);
		assertEquals("Alvin Lnu", result.getName());
		assertEquals("1234567890", result.getMobileNumber());
		assertEquals("alvin.lnu@example.com", result.getEmail());
		assertEquals(30, result.getAge());
		assertNotNull(result.getAccountNumber());
		verify(userRepository, times(1)).save(any(User.class));
		verify(restTemplate, times(1)).postForObject(eq("http://localhost:8082/accounts/create"), any(AccountDTO.class),
				eq(Void.class));
	}

	@Test
	void testGetUserById() {
		// Arrange
		User user = new User();
		user.setId(1L);
		user.setName("Alvin Lnu");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		// Act
		Optional<User> result = userService.getUserById(1L);

		// Assert
		assertTrue(result.isPresent());
		assertEquals("Alvin Lnu", result.get().getName());
	}

	@Test
	void testUpdateUser() {
		// Arrange
		UserDTO userDTO = new UserDTO();
		userDTO.setName("Kelvin Raj");
		userDTO.setMobileNumber("0987654321");
		userDTO.setEmail("kelvin.raj@example.com");
		userDTO.setAge(28);

		User user = new User();
		user.setId(1L);
		user.setName("Alvin Lnu");
		user.setMobileNumber("1234567890");
		user.setEmail("alvin.lnu@example.com");
		user.setAge(30);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);

		// Act
		User result = userService.updateUser(1L, userDTO);

		// Assert
		assertNotNull(result);
		assertEquals("Kelvin Raj", result.getName());
		assertEquals("0987654321", result.getMobileNumber());
		assertEquals("kelvin.raj@example.com", result.getEmail());
		assertEquals(28, result.getAge());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testGetAccountDTOByUserId() {
		// Arrange
		User user = new User();
		user.setId(1L);
		user.setAccountNumber("123456789012");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		// Act
		AccountDTO result = userService.getAccountDTOByUserId(1L);

		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getUserId());
		assertEquals("123456789012", result.getAccountNumber());
	}
}
