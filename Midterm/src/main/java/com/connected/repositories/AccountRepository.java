package com.connected.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connected.domain.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByUserName(String userName);

	Optional<Account> findByProfileString(String profileString);

}
