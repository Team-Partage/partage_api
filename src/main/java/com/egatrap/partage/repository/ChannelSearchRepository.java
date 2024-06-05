package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChannelSearchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelSearchRepository extends JpaRepository<ChannelSearchEntity, String> {

    @Query("SELECT cs from ChannelSearchEntity cs where cs.hashtag like %:keyword% or cs.titles like %:keyword%")
    Page<ChannelSearchEntity> searchByKeyword(String keyword, PageRequest pageRequest);
}
