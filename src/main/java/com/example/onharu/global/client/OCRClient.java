package com.example.onharu.global.client;

import com.example.onharu.global.util.MultipartInputStreamFileResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", getRequestPayload());
            body.add("file", new MultipartInputStreamFileResource(image));

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(ocrUrl, request,
                    String.class);

            return parseInferTexts(response.getBody()); // inferText만 이어붙이기

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

    private String parseInferTexts(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        StringBuilder sb = new StringBuilder();

        for (JsonNode field : root.path("images").get(0).path("fields")) {
            sb.append(field.path("inferText").asText()).append(" ");
        }

        return sb.toString().trim();
    }

}
