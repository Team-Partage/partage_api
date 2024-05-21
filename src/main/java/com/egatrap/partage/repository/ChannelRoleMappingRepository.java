package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChannelRoleMappingEntity;
import com.egatrap.partage.model.entity.ChannelRoleMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRoleMappingRepository extends JpaRepository<ChannelRoleMappingEntity, ChannelRoleMappingId> {

    @Query("SELECT CASE WHEN COUNT(crm) > 0 THEN true ELSE false END " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN crm.channel c " +
            "WHERE crm.id.userNo = :userNo " +
            "AND crm.id.roleId = :roleId " +
            "AND c.isActive = true")
    boolean isExistsActiveChannelByUserNo(@Param("userNo") Long userNo, @Param("roleId") String roleId);
}
