package com.egatrap.partage.repository;

import com.egatrap.partage.model.dto.ChannelUserInfoDto;
import com.egatrap.partage.model.entity.ChannelRoleMappingEntity;
import com.egatrap.partage.model.entity.ChannelRoleMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface ChannelRoleMappingRepository extends JpaRepository<ChannelRoleMappingEntity, ChannelRoleMappingId> {

    @Query("SELECT CASE WHEN COUNT(crm) > 0 THEN true ELSE false END " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN crm.channel c " +
            "WHERE crm.id.userId = :userId " +
            "AND crm.role.roleId = :roleId " +
            "AND c.isActive = true")
    boolean isExistsActiveChannelByUserId(@Param("userId") String userId, @Param("roleId") String roleId);

    @Query("SELECT c.isActive " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN crm.channel c " +
            "WHERE crm.id.userId = :userId " +
            "AND crm.role.roleId = :roleId " +
            "AND c.channelId = :channelId")
    Boolean isActiveChannelByOwnerUserIdAndChannelId(String userId, String roleId, String channelId);

    @Query("SELECT new com.egatrap.partage.model.dto.ChannelUserInfoDto(crm.role.roleId, u.userId, u.email, u.nickname, " +
            "u.profileColor, u.profileImage) " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN UserEntity u ON crm.id.userId = u.userId " +
            "WHERE crm.id.channelId = :channelId")
    List<ChannelUserInfoDto> findActiveUsersByChannelId(@Param("channelId") String channelId);

    @Query("SELECT c.isActive " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN crm.channel c " +
            "WHERE crm.id.userId = :userId " +
            "AND c.channelId = :channelId")
    Boolean isActiveChannelByUserIdAndChannelId(String userId, String channelId);

    @Query("SELECT new com.egatrap.partage.model.dto.ChannelUserInfoDto(crm.role.roleId, u.userId, u.email, u.nickname, " +
            "u.profileColor, u.profileImage) " +
            "FROM ChannelRoleMappingEntity crm " +
            "JOIN UserEntity u ON crm.id.userId = u.userId " +
            "WHERE crm.id.channelId = :channelId " +
            "AND crm.id.userId = :userId")
    ChannelUserInfoDto findActiveUserByChannelIdAndUserId(@Param("channelId") String channelId, @Param("userId") String userId);

    Optional<ChannelRoleMappingEntity> findByUser_UserId(String userId);

    Optional<ChannelRoleMappingEntity> findByUser_UserIdAndChannel_ChannelId(String userId, String channelId);

    @Modifying
    @Query("UPDATE ChannelRoleMappingEntity crm SET crm.role.roleId = :roleId " +
            "WHERE crm.id.channelId = :channelId " +
            "AND crm.id.userId = :userId")
    int updateRoleId(@Param("channelId") String channelId, @Param("userId") String userId, @Param("roleId") String roleId);

    @Query("SELECT crm FROM ChannelRoleMappingEntity crm WHERE crm.channel.channelId = :channelId AND crm.user.userId = :userId")
    Optional<ChannelRoleMappingEntity> findByChannelIdAndUserId(String channelId, String userId);


    void deleteByChannel_ChannelId(String channelId);
}
