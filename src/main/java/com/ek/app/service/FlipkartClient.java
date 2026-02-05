package com.ek.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ek.app.scheduler.Shipment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@Slf4j
public class FlipkartClient {

    @Value("${flipkart.api.base}")
    private String baseUrl;

    @Value("${flipkart.api.token:}")
    private String token;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<com.ek.app.scheduler.Shipment> fetchPendingLabelShipments() throws IOException {
        // NOTE: Replace endpoint path with real Flipkart shipments endpoint as per their docs
        String url = baseUrl + "/sellers/v3/shipments?filter=pendingLabel";
        Request req = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                log.error("Failed to fetch shipments: " + resp.code() + " " + resp.message());
                return new ArrayList<>();
            }
            String body = resp.body().string();
            JsonNode root = mapper.readTree(body);
            // Parse according to actual Flipkart response structure. Here is a placeholder parse.
            List<com.ek.app.scheduler.Shipment> shipments = new ArrayList<>();
            if (root.isArray()) {
                for (JsonNode node : root) {
                    Shipment s = new Shipment();
                    s.setShipmentId(node.path("shipmentId").asText());
                    s.setOrderId(node.path("orderId").asText());
                    shipments.add(s);
                }
            } else {
                // adapt parsing
            }
            return shipments;
        }
    }

    public byte[] generateLabelPdf(String shipmentId) throws IOException {
        // Use Flipkart label endpoint - placeholder
        String url = baseUrl + "/sellers/v3/shipments/" + shipmentId + "/labels";
        Request req = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create("{}", MediaType.parse("application/json")))
                .build();

        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                log.error("Label generation failed for {}: {} {}", shipmentId, resp.code(), resp.message());
                return null;
            }
            return resp.body().bytes();
        }
    }

    public boolean markShipmentRTD(String shipmentId) throws IOException {
        String url = baseUrl + "/sellers/v3/shipments/" + shipmentId + "/dispatch";
        Request req = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create("{}", MediaType.parse("application/json")))
                .build();

        try (Response resp = client.newCall(req).execute()) {
            return resp.isSuccessful();
        }
    }
}
