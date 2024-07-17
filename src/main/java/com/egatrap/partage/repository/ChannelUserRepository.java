package com.egatrap.partage.repository;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.entity.ChannelUserEntity;
import com.egatrap.partage.model.entity.ChannelUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ChannelUserRepository extends JpaRepository<ChannelUserEntity, ChannelUserId> {

//      Pessimistic Locking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    Optional<ChannelUserEntity> findById(ChannelUserId id);

    /**
     * 채널 사용자 정보 저장
     * @param sessionId 세션 ID
     * @param channelId 채널 ID
     * @param userId 사용자 ID
     * @param roleId 채널 권한 ID
     * @param isOnline 채널 접속 여부
     * @param createAt 생성 일시
     * @param lastAccessAt 최근 접속 일시
     */
    @Modifying
    @Transactional
    @Query(value =
            "INSERT INTO tb_channel_user (" +
                    "    session_id, " +
                    "    channel_id, " +
                    "    user_id, " +
                    "    role_id, " +
                    "    online_count, " +
                    "    create_at, " +
                    "    last_access_at" +
                    ") VALUES (" +
                    "    :sessionId, " +
                    "    :channelId, " +
                    "    :userId, " +
                    "    :roleId, " +
                    "    :onlineCount, " +
                    "    :createAt, " +
                    "    :lastAccessAt" +
                    ")",
            nativeQuery = true
    )
    void saveChannelUser(
            @Param("sessionId") String sessionId,
            @Param("channelId") String channelId,
            @Param("userId") String userId,
            @Param("roleId") String roleId,
            @Param("onlineCount") Long onlineCount,
            @Param("createAt") LocalDateTime createAt,
            @Param("lastAccessAt") LocalDateTime lastAccessAt
    );

    long countById_ChannelIdAndOnlineCountGreaterThan(String channelId, long onlineCount);

    long countByOnlineCountGreaterThan(long onlineCount);

    @Query("SELECT cu FROM ChannelUserEntity cu WHERE cu.channel.channelId = :channelId AND cu.user.userId = :userId AND cu.onlineCount >= 1")
    Optional<ChannelUserEntity> findByChannel_ChannelIdAndUser_UserIdAndOnlineCountGreaterThanOne(
            @Param("channelId") String channelId,
            @Param("userId") String userId);

    @Query("SELECT cu FROM ChannelUserEntity cu WHERE cu.channel.channelId = :channelId AND cu.onlineCount >= 1")
    Page<ChannelUserEntity> findByChannel_ChannelIdAndOnlineCountGreaterThanOne(String channelId, PageRequest pageRequest);
}
