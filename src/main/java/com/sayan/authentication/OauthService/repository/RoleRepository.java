package com.sayan.authentication.OauthService.repository;

import org.springframework.data.repository.CrudRepository;

import com.sayan.authentication.OauthService.model.Role;


public interface RoleRepository extends CrudRepository<Role, Long> {
}