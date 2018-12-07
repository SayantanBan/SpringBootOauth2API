package com.sayan.authentication.OauthService.resource;


import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.sayan.authentication.OauthService.config.SecurityConfig;
import com.sayan.authentication.OauthService.model.Role;
import com.sayan.authentication.OauthService.model.User;
import com.sayan.authentication.OauthService.model.UserRole;
import com.sayan.authentication.OauthService.repository.UserRepository;
import com.sayan.authentication.OauthService.security.SecurityUtility;
import com.sayan.authentication.OauthService.service.GenericService;
import com.sayan.authentication.OauthService.utility.MailConstructor;


@RestController
@EnableResourceServer
@RequestMapping("/user")
public class UserResource {

	@Autowired
	private GenericService genericService;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailConstructor mailConstructor;

	@Autowired
	private JavaMailSender mailSender;

	@CrossOrigin(origins = { "http://localhost:4200", "https://eutivityrestaurantcrm365.firebaseapp.com" })
	@RequestMapping(value="/getCurrentUser", method = RequestMethod.POST)
	public ResponseEntity getCurrentUser(HttpServletRequest request, @RequestBody HashMap<String, String> mapper) {

		User user = genericService.findByUsername(mapper.get("username"));
		System.out.println(mapper.get("username"));
		if (user == null) {
			return new ResponseEntity("Email not found", HttpStatus.BAD_REQUEST);
		}
		else
		{
			return new ResponseEntity(user,HttpStatus.OK);
		}
		

	}

	@CrossOrigin(origins = { "http://localhost:4200", "https://eutivityrestaurantcrm365.firebaseapp.com" })
	@RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
	public ResponseEntity forgetPasswordPost(HttpServletRequest request, @RequestBody HashMap<String, String> mapper)
			throws Exception {

		User user = genericService.findByUsername(mapper.get("email"));

		if (user == null) {
			return new ResponseEntity("Email not found", HttpStatus.BAD_REQUEST);
		}
		String password = SecurityUtility.randomPassword();

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);
		genericService.save(user);

		SimpleMailMessage newEmail = mailConstructor.constructNewUserEmail(user, password);
		mailSender.send(newEmail);

		return new ResponseEntity("Email sent!", HttpStatus.OK);

	}

	@CrossOrigin(origins = { "http://localhost:4200", "https://eutivityrestaurantcrm365.firebaseapp.com" })
	@RequestMapping(value="/updateUserInfo", method=RequestMethod.POST)
	public ResponseEntity profileInfo(
				@RequestBody HashMap<String, Object> mapper
			) throws Exception{
		
		int id = (Integer) mapper.get("id");
		String email = (String) mapper.get("email");
		String username = (String) mapper.get("username");
		String firstName = (String) mapper.get("firstName");
		String lastName = (String) mapper.get("lastName");
		String newPassword = (String) mapper.get("newPassword");
		String currentPassword = (String) mapper.get("currentPassword");
		
		User currentUser = genericService.findById(Long.valueOf(id));
		
		System.out.println(currentUser.getFirstName());
		
		if(currentUser == null) {
			throw new Exception ("User not found");
		}
		
		if(genericService.findByUsername(username) != null) {
			if(genericService.findByUsername(username).getId() != currentUser.getId()) {
				return new ResponseEntity("Email not found!", HttpStatus.BAD_REQUEST);
			}
		}
		
		if(genericService.findByUsername(username) != null) {
			if(genericService.findByUsername(username).getId() != currentUser.getId()) {
				return new ResponseEntity("Username not found!", HttpStatus.BAD_REQUEST);
			}
		}
		
		SecurityConfig securityConfig = new SecurityConfig();
		
		
			BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
			String dbPassword = currentUser.getPassword();
			
			if(null != currentPassword)
			if(passwordEncoder.matches(currentPassword, dbPassword)) {
				if(newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")) {
					currentUser.setPassword(passwordEncoder.encode(newPassword));
				}
				currentUser.setEmail(email);
			} else {
				return new ResponseEntity("Incorrect current password!", HttpStatus.BAD_REQUEST);
			}
		
		
		currentUser.setFirstName(firstName);
		currentUser.setLastName(lastName);
		currentUser.setUsername(username);
		
		
		genericService.save(currentUser);
		
		return new ResponseEntity("Update Success", HttpStatus.OK);
	}

	@CrossOrigin(origins = { "http://localhost:4200", "https://eutivityrestaurantcrm365.firebaseapp.com" })
	@RequestMapping(value = "/newUser", method = RequestMethod.POST)
	public ResponseEntity<String> newUserPost(HttpServletRequest request, @RequestBody HashMap<String, String> mapper)
			throws Exception {
		String username = mapper.get("username");
		String firstName = mapper.get("firstname");
		String lastName = mapper.get("lastname");

		if (userRepository.findByUsername(username) != null) {
			return new ResponseEntity<String>("usernameExists", HttpStatus.BAD_REQUEST);
		}
		else {
			User user = new User();
			user.setUsername(username);
			user.setEmail(username);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			
			String password = SecurityUtility.randomPassword();

			String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
			user.setPassword(encryptedPassword);
						
			userRepository.save(user);

			Role role = new Role();
			role.setName("ROLE_USER");
			Set<UserRole> userRoles = new HashSet<>();
			userRoles.add(new UserRole(user, role));
			genericService.createUser(user, userRoles);
			
			SimpleMailMessage email = mailConstructor.constructNewUserEmail(user, password);
			mailSender.send(email);

			return new ResponseEntity<String>("User Added Successfully!", HttpStatus.OK);

		}
		
	}
}
