package com.ek.app.mcp;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mcp")
public class MCPController {

    private final LLMService llmService;

    public MCPController(LLMService llmService) {
        this.llmService = llmService;
    }

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> chat(@RequestBody PromptRequest request) {
       // String output = llmService.chat(request.prompt());
        String output = "sample response for prompt: " + request.prompt() + " (Replace this with actual LLM response)";
        
        

        return Map.of(
                "prompt", request.prompt(),
                "response", output
        );
    }

    @PostMapping(value = "/structured-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> structuredData(@RequestBody PromptRequest request) {
        return llmService.structuredDataResponse(request.prompt());
    }

    public record PromptRequest(String prompt) {
    }
}
