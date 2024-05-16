package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChattingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRepository extends JpaRepository<ChattingEntity, Long> {
}
