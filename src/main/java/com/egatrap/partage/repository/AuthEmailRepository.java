package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.AuthEmail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthEmailRepository extends CrudRepository<AuthEmail, String> {
}