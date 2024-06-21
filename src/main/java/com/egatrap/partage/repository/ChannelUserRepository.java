package com.egatrap.partage.repository;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.entity.ChannelUserEntity;
import com.egatrap.partage.model.entity.ChannelUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface ChannelUserRepository extends JpaRepository<ChannelUserEntity, ChannelUserId> {

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
                    "    is_online, " +
                    "    create_at, " +
                    "    last_access_at" +
                    ") VALUES (" +
                    "    :sessionId, " +
                    "    :channelId, " +
                    "    :userId, " +
                    "    :roleId, " +
                    "    :isOnline, " +
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
            @Param("isOnline") Boolean isOnline,
            @Param("createAt") LocalDateTime createAt,
            @Param("lastAccessAt") LocalDateTime lastAccessAt
    );

}
