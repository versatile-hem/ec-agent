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
# ec-agent
