package com.egatrap.partage.service;

import com.egatrap.partage.common.util.CodeGenerator;
import com.egatrap.partage.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service("fileService")
@RequiredArgsConstructor
public class FileService {

    @Value("${file.profile-images}")
    private String profileImageDir;

    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg");
    // private final int PROFILE_IMAGE_MAX_WIDTH = 1920;
    // private final int PROFILE_IMAGE_MAX_HEIGHT = 1080;

    public String saveProgileImage(MultipartFile profileImage) {
        // 이미지 확장자 체크
        String originFilename = profileImage.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originFilename);
        if (!isValidExtension(extension))
            throw new BadRequestException("Unsupported file format. Only png, jpg, jpeg are allowed.");

        // ToDo. 이미지 사이즈 체크
        //  - 이미지 용량 체크를 진행하므로 일단 보류

        // 상대경로 사용을 지양하지만 프로젝트 특성상 OS가 다르고 도커를 사용하기도 하여 일단 프로젝트 내 경로를 사용
        Path path = Paths.get(profileImageDir).toAbsolutePath().normalize();

        // 이미지 저장
        String saveFilename = CodeGenerator.generateID("I") + "." + extension;
        try {
            // directory가 없으면 생성
            Files.createDirectories(path);
            profileImage.transferTo(new File(path.toString() + "/" + saveFilename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to profile image save error.", e);
        }
        return saveFilename;
    }

    private boolean isValidExtension(String extension) {
        // 이미지 확장자 체크
        if (extension != null)
            return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
        return false;
    }
}
