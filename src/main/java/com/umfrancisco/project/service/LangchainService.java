package com.umfrancisco.project.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

@Service
public class LangchainService {
	
	@Value("${api.ollama.ipv4}")
	public String ipv4;
	
	public String run(String prompt) {
		
		ChatLanguageModel model = OllamaChatModel.builder()
				.baseUrl(ipv4)
				.modelName("phi3:mini")
				.timeout(Duration.ofMinutes(2))
				.temperature(0.7)
				.logRequests(true)
				.logResponses(true)
				.build();
		
		return chat(model, prompt);
	}
	
	public static String chat(ChatLanguageModel model, String prompt) {
		System.out.println("["+getDateTime()+"] Prompt: " + prompt);
        System.out.println("-- Sending request to Ollama -- ");
        
        try {
            String response = model.generate(prompt);
            System.out.println("\n["+getDateTime()+"] Response:\n"+response+"\n");
            return response;
        } catch (Exception e) {
            System.err.println("Error communicating with Ollama: " + e.getMessage());
            System.err.println("Ensure Ollama is actively running on port 9090 and the model is pulled.");
        }
        return "ERROR";
	}
	
	public static String getDateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return now.format(formatter);
	}
	
}
