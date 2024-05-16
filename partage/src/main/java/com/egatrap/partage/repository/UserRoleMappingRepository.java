package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.UserRoleMappingEntity;
import com.egatrap.partage.model.entity.UserRoleMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMappingEntity, UserRoleMappingId> {
}
