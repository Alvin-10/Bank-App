package com.alvin.project.Repository;

import com.alvin.project.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	//query derivation feature
    User findByAccountNumber(String accountNumber);
}