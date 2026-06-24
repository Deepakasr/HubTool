package com.tool.hub.serviceImpl;

import com.tool.hub.service.EmailService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Override
    public void sendOtp(String email, String otp) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("api-key", brevoApiKey);
        System.out.println("BREVO KEY = " + brevoApiKey);

        Map<String, Object> requestBody = Map.of(
            "sender", Map.of(
                "name", "ToolHub",
                "email", "dk88107765@gmail.com"
            ),
            "to", List.of(
                Map.of("email", email)
            ),
            "subject", "ToolHub Email Verification",
            "htmlContent",
            "<h2>Your OTP is: " + otp +
            "</h2><p>This OTP is valid for 5 minutes.</p>"
        );

        HttpEntity<Map<String, Object>> request =
            new HttpEntity<>(requestBody, headers);

       try {

    ResponseEntity<String> response =
            restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    request,
                    String.class
            );

    System.out.println("SUCCESS = " + response.getBody());

} catch (org.springframework.web.client.HttpClientErrorException e) {

    System.out.println("STATUS = " + e.getStatusCode());
    System.out.println("BODY = " + e.getResponseBodyAsString());

    throw e;
}
    }
}
