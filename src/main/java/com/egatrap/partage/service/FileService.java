package com.egatrap.partage.service;

import com.egatrap.partage.common.util.CodeGenerator;
import com.egatrap.partage.exception.BadRequestException;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service("fileService")
@RequiredArgsConstructor
public class FileService {

    @Value("${file.profile-images}")
    private String profileImageDir;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    private final Storage storage;

    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg");
    private final String STORAGE_BASE_URL = "https://storage.googleapis.com/";
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

        // Cloud에 이미지 업로드
        String uuid = CodeGenerator.generateID("I");
        try {
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder(bucketName, uuid)
                            .setContentType(extension)
                            .build(),
                    profileImage.getInputStream()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to profile image save error.", e);
        }

        String url = STORAGE_BASE_URL + bucketName + "/" + uuid;
        return url;
    }

    private boolean isValidExtension(String extension) {
        // 이미지 확장자 체크
        if (extension != null)
            return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
        return false;
    }
}
