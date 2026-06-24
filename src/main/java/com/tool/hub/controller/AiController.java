package com.tool.hub.controller;

import com.tool.hub.dto.AiRequestDto;
import com.tool.hub.service.AiService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@CrossOrigin("*")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody AiRequestDto dto) {
        try {
            return aiService.chat(dto.getPrompt());
        } catch (Exception e) {
            e.printStackTrace();

            return e.getMessage();
        }
    }

    @PostMapping("/generate-image")
    public String generateImage(@RequestBody AiRequestDto dto) {
        return aiService.generateImage(dto.getPrompt());
    }
}
