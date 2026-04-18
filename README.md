# Flipkart Label Agent (Spring Boot) - Starter

This starter project is a Spring Boot application that:
- Fetches shipments with pending labels from Flipkart (placeholder endpoints)
- Generates label PDFs via Flipkart API
- Prints labels to a local TSC/USB printer using Java PrintService
- Marks shipments as RTD on Flipkart after successful print
- Sends email alerts (Gmail) on failures (printing, label gen, etc.)

## How to use

1. Set environment variable FLIPKART_TOKEN with your API token.
2. Update `src/main/resources/application.properties`:
   - printer.name to match your TSC printer name as listed by the OS.
   - spring.mail.username and spring.mail.password (Gmail app password).
   - alert.recipients.
3. Build and run:
   ```
   mvn clean package
   java -jar target/flipkart-label-agent-0.0.1-SNAPSHOT.jar
   ```

## Notes
- Flipkart API endpoints in `FlipkartClient` are placeholders; replace with actual endpoints from Flipkart Seller API docs.
- For TSC printers, if PDFs do not print correctly, consider converting PDF to image or sending TSPL commands.
- For USB printers on servers, ensure the JVM has access to the local print spooler and the printer drivers are installed.

## MCP API (OpenAI)

Set OpenAI API key before running:

```bash
export OPENAI_API_KEY=your_openai_api_key
```

Chat endpoint:

```bash
curl --noproxy '*' -sS -X POST 'http://127.0.0.1:80/api/mcp/chat' \
   -H 'Content-Type: application/json' \
   -d '{"prompt":"Say hello in one line"}'
```

Structured data endpoint:

```bash
curl --noproxy '*' -sS -X POST 'http://127.0.0.1:80/api/mcp/structured-data' \
   -H 'Content-Type: application/json' \
   -d '{"prompt":"Return JSON with fields title, priority, etaDays"}'
```

Expected behavior:
- `/api/mcp/chat` returns JSON with `prompt` and `response`.
- `/api/mcp/structured-data` returns model-generated JSON object.

If you receive redirect to `/login`, update security to permit `/api/mcp/**` for unauthenticated access.
# ec-agent
