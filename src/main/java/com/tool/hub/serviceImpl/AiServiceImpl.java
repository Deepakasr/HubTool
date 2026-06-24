package com.tool.hub.serviceImpl;

import com.tool.hub.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiServiceImpl implements AiService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public AiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String chat(String prompt) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        String requestBody = """
            {
              "model":"openai/gpt-oss-20b:free",
              "messages":[
                {
                  "role":"user",
                  "content":"%s"
                }
              ]
            }
            """.formatted(prompt);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.setBearerAuth(apiKey);

        headers.add("HTTP-Referer", "http://localhost:5174");

        headers.add("X-Title", "ToolHub");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url, entity, String.class);
    }

    @Override
    public String generateImage(String prompt) {
        return (
            "https://image.pollinations.ai/prompt/" + prompt.replace(" ", "%20")
        );
    }
}
