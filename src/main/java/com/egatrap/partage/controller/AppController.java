package com.egatrap.partage.controller;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class AppController {

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

}
