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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    private final int PROFILE_IMAGE_MIN_WIDTH = 240;
    private final int PROFILE_IMAGE_MIN_HEIGHT = 240;
    private final int PROFILE_IMAGE_MAX_WIDTH = 4000;   // 임시 맥스 사이즈
    private final int PROFILE_IMAGE_MAX_HEIGHT = 4000;  // 임시 맥스 사이즈

    public String saveProgileImage(MultipartFile profileImage) {
        // 이미지 확장자 체크
        String originFilename = profileImage.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originFilename);
        if (!isValidExtension(extension))
            throw new BadRequestException("Unsupported file format. Only png, jpg, jpeg are allowed.");

        // 이미지 사이즈 체크
        if (!isValidImageSize(profileImage))
            throw new BadRequestException("The minimum/maximum width, less than or above height of the image.");

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

    private boolean isValidImageSize(MultipartFile profileImage) {

        BufferedImage image = null;
        try {
            image = ImageIO.read(profileImage.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to profile image load error.", e);
        }

        int width = image.getWidth();
        int height = image.getHeight();

        // 이미지 최소 사이즈 또는 최대 사이즈 기준에 미충족
        if (width < PROFILE_IMAGE_MIN_WIDTH || width > PROFILE_IMAGE_MAX_WIDTH ||
            height < PROFILE_IMAGE_MIN_HEIGHT || height > PROFILE_IMAGE_MAX_HEIGHT)
            return false;

        return true;
    }
}
