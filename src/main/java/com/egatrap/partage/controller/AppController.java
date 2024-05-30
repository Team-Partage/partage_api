package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.service.ChannelPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppController {

    private final ChannelPermissionService channelPermissionService;

    @Value("${jasypt.encryptor.key}")
    private String key;

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/encrypt")
    @Profile("local")
    public String encrypt(@RequestBody String rawData) throws Exception {
        log.debug("[rawData]=[{}]", rawData);
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(this.key);
        return pbeEnc.encrypt(rawData);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {

        ChannelRoleType roleType = channelPermissionService.getChannelRole("C-fe9c2d3591c64800962d9fc08eb54e", "U-af7a87726be14113af85418d67778d");
        log.debug("[roleType]=[{}]", roleType);

        return ResponseEntity.ok(channelPermissionService.getChannelPermission("C-03d55a4e49f04c03aea01dc35c71a2"));
    }

}
