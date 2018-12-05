package com.sayan.authentication.OauthService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.sayan.authentication.OauthService.model.User;


public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
	List<User> findAll();
}