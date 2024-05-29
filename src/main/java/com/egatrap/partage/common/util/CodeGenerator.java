package com.egatrap.partage.common.util;

import java.security.SecureRandom;
import java.util.UUID;

public class CodeGenerator {

    // 가능한 문자들을 담은 문자열
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // SecureRandom 객체 생성
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * 랜덤 문자열 생성
     *
     * @param length 생성할 문자열 길이
     * @return 랜덤 문자열
     */
    public static String generateCode(int length) {
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // CHARACTERS 문자열에서 무작위로 하나의 문자를 선택하여 추가
            builder.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }

        return builder.toString();
    }

    /**
     * ID 생성
     *
     * @param code 4자리 코드
     * @param type 4자리 타입
     * @return ID
     */
    public static String generateID(String prefix) {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        String prefixWithHyphen = prefix + "-";

        int remainingLength = 32 - prefixWithHyphen.length();

        // 남은 길이가 UUID 길이를 초과하지 않도록 검사
        if (remainingLength < 0) {
            throw new IllegalArgumentException(
                    "Prefix and hyphen combined are too long. Must be less than 32 characters.");
        }

        // UUID에서 필요한 길이만큼 잘라내어 prefixWithHyphen과 결합
        return prefixWithHyphen + uuid.substring(0, remainingLength);
    }

}