package com.sayan.authentication.OauthService.service;


import java.util.List;
import java.util.Set;

import com.sayan.authentication.OauthService.model.User;
import com.sayan.authentication.OauthService.model.UserRole;


public interface GenericService {
        
    List<User> findAllUsers();
	
	User createUser(User user, Set<UserRole> userRoles);
	
	User save(User user);

	User findByUsername(String username);

	User findById(Long id);
}