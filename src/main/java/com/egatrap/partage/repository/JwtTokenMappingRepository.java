package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.JwtTokenMappingEntity;
import org.springframework.data.repository.CrudRepository;

public interface JwtTokenMappingRepository extends CrudRepository<JwtTokenMappingEntity, String> {
}
