package com.egatrap.partage.common.util;

import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CachingRequestBodyWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public CachingRequestBodyWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 요청 본문을 바이트 배열로 캐싱합니다.
        this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);

        // ServletInputStream은 추상 클래스이므로 익명 클래스를 사용하여 인스턴스를 생성합니다.
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException("Not implemented");
            }
        };
    }

    // 요청 본문을 String으로 반환하는 편의 메서드를 제공할 수 있습니다.
    public String getRequestBody() throws IOException {
        return new String(this.cachedBody, this.getCharacterEncoding());
    }

}
