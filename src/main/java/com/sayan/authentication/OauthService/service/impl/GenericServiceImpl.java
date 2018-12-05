package com.sayan.authentication.OauthService.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sayan.authentication.OauthService.model.User;
import com.sayan.authentication.OauthService.model.UserRole;
import com.sayan.authentication.OauthService.repository.RoleRepository;
import com.sayan.authentication.OauthService.repository.UserRepository;
import com.sayan.authentication.OauthService.service.GenericService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class GenericServiceImpl implements GenericService {
	
	private static final Logger LOG = LoggerFactory.getLogger(GenericServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private RoleRepository roleRepository;

    @Override
    public List<User> findAllUsers() {
        return (List<User>)userRepository.findAll();
    }

    @Override
	public User save(User user)  {
		return userRepository.save(user);
	}
	
	@Transactional
	public User createUser(User user, Set<UserRole> userRoles) {
		User localUser = new User();
		
		for (UserRole ur : userRoles) {
			roleRepository.save(ur.getRole());
			
		user.getUserRoles().addAll(userRoles);
			
		localUser = userRepository.save(user);
		}
		
		return localUser;
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}
	

}
