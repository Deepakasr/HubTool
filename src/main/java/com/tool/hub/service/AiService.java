package com.tool.hub.service;

public interface AiService {
    // Generate a chat response based on the given prompt
    String chat(String prompt) throws Exception;

    // Generate an image based on the given prompt
    String generateImage(String prompt);
}
