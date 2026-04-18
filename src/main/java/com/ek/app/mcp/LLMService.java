package com.ek.app.mcp;

import java.util.Map;

public interface LLMService {

    String chat(String prompt);

    Map<String, Object> structuredDataResponse(String prompt);
}
