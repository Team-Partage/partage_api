package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.UserRoleEntity;
import com.egatrap.partage.constants.UserRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleType> {
}
