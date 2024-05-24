package com.egatrap.partage.repository;

import com.egatrap.partage.model.dto.ChannelUserInfoDto;
import com.egatrap.partage.model.entity.ChannelRoleMappingEntity;
import com.egatrap.partage.model.entity.ChannelRoleMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public interface ChannelRoleMappingRepository extends JpaRepository<ChannelRoleMappingEntity, ChannelRoleMappingId> {

    @Query("SELECT CASE WHEN COUNT(crm) > 0 THEN true ELSE false END " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN crm.channel c " +
            "WHERE crm.id.userNo = :userNo " +
            "AND crm.id.roleId = :roleId " +
            "AND c.isActive = true")
    boolean isExistsActiveChannelByUserNo(@Param("userNo") Long userNo, @Param("roleId") String roleId);

    @Query("SELECT c.isActive " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN crm.channel c " +
            "WHERE crm.id.userNo = :userNo " +
            "AND crm.id.roleId = :roleId " +
            "AND crm.isActive = true " +
            "AND c.channelNo = :channelNo")
    Boolean isActiveChannelByOwnerUserNoAndChannelNo(Long userNo, String roleId, Long channelNo);

    @Query("SELECT new com.egatrap.partage.model.dto.ChannelUserInfoDto(crm.id.roleId, u.userNo, u.email, u.nickname, " +
            "u.profileColor, u.profileImage) " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN UserEntity u ON crm.id.userNo = u.userNo " +
            "WHERE crm.id.channelNo = :channelNo " +
            "AND crm.isActive = true")
    List<ChannelUserInfoDto> findActiveUsersByChannelNo(@Param("channelNo") Long channelNo);

    @Query("SELECT c.isActive " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN crm.channel c " +
            "WHERE crm.id.userNo = :userNo " +
            "AND crm.isActive = true " +
            "AND c.channelNo = :channelNo")
    Boolean isActiveChannelByUserNoAndChannelNo(Long userNo, Long channelNo);

    @Query("SELECT new com.egatrap.partage.model.dto.ChannelUserInfoDto(crm.id.roleId, u.userNo, u.email, u.nickname, " +
            "u.profileColor, u.profileImage) " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN UserEntity u ON crm.id.userNo = u.userNo " +
            "WHERE crm.id.channelNo = :channelNo " +
            "AND crm.id.userNo = :userNo " +
            "AND crm.isActive = true")
    ChannelUserInfoDto findActiveUserByChannelNoAndUserNo(@Param("channelNo") Long channelNo, @Param("userNo") Long userNo);
}
