package com.egatrap.partage.service;

import com.egatrap.partage.repository.ChannelRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("channelRoleService")
@RequiredArgsConstructor
public class ChannelRoleService {

    private final ChannelRoleRepository channelRoleRepository;


}
