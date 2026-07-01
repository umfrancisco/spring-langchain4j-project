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

//  NAME                ID              SIZE      MODIFIED           
//  gemma3:1b           8648f39daa8f    815 MB    59 seconds ago        
//  llama3.2:1b         baf6a787fdff    1.3 GB    About a minute ago    
//  phi4-mini:latest    78fad5d182a7    2.5 GB    22 hours ago          
//  phi3:mini           4f2222927938    2.2 GB    2 days ago            
//  smallthinker:3b     945eb1864589    3.6 GB    3 days ago            
//  qwen3.5:9b          6488c96fa5fa    6.6 GB    4 days ago 

@Service
public class LangchainService {
	
	@Value("${api.ollama.url}")
	public String url;
	public String runningModel = "phi4-mini:latest";
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
				.modelName(runningModel)
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
	
	public String chat(ChatLanguageModel model, String prompt) {
		System.out.println("Model: "+runningModel);
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
	
	public String getDateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return now.format(formatter);
	}
	
}
