package com.example.onharu.global.client;

import com.example.onharu.global.util.MultipartInputStreamFileResource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class OCRClient {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${naver.ocr.url}")
    private String ocrUrl;
    @Value("${naver.ocr.secret}")
    private String secretKey;

    public String extractText(MultipartFile image) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-OCR-SECRET", secretKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
            multipartBody.add("message", getRequestPayload());
            multipartBody.add("file", new MultipartInputStreamFileResource(image));

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multipartBody,
                    headers);

            ResponseEntity<String> response = restTemplate.postForEntity(ocrUrl, request,
                    String.class);

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isBlank()) {
                throw new RuntimeException("OCR 응답이 비어 있습니다.");
            }

            return responseBody;

        } catch (Exception e) {
            throw new RuntimeException("OCR 호출 실패: " + e.getMessage());
        }
    }

    private String getRequestPayload() {
        return """
                {
                  "version":"V2",
                  "requestId": "req-0001",
                  "timestamp": 0,
                  "images": [ {"format":"jpg","name":"prescription"} ]
                }
                """;
    }
}
