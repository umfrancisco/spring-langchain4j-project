package com.umfrancisco.project.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.umfrancisco.project.model.ProductDTO;
import com.umfrancisco.project.repository.ProductRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

@Service
public class LangchainService {
	
	@Value("${api.ollama.url}")
	public String url;
	private ProductRepository repository;
	
	public LangchainService(ProductRepository repository) {
		this.repository = repository;
	}
	
	public String run(String questions) {
		String prompt = """
				Instructions:
				Be concise. Use ONLY this data.
				
				Data:
				%s
				
				Questions:
				%s
				""";
		var products = getProducts();
		
		if (products.isEmpty()) {
			return "Empty database";
		}
		
		ChatLanguageModel model = OllamaChatModel.builder()
				.baseUrl(url)
				.modelName("phi3:mini")
				.timeout(Duration.ofMinutes(15))
				.temperature(0.0)
				.logRequests(true)
				.logResponses(true)
				.build();
		
		return chat(model, prompt.formatted(products, questions));
	}
	
	public List<ProductDTO> getProducts() {
		List<ProductDTO> products = new ArrayList<>();
		for (var p : repository.findAll()) {
			products.add(new ProductDTO(p.getId(), p.getName(), p.getPrice(), p.getStock()));
		}
		return products;
	}
	
	public static String chat(ChatLanguageModel model, String prompt) {
		System.out.println("["+getDateTime()+"] Prompt:\n" + prompt);
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
