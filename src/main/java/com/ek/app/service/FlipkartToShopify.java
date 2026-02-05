package com.ek.app.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FlipkartToShopify {

	public static void main(String[] args) {
		try {
			disableSSLVerification();

			// Step 1: Crawl Flipkart Product Page
			String flipkartUrl = "https://www.flipkart.com/earendelkids-foldable-baby-potty-seat-cute-frog-design-non-slip-portable-trainer-seat/p/itm041167906742b?pid=PSBHGAP4YCZ4689X";
			Document doc = Jsoup.connect(flipkartUrl).get();

			// Extract product title
			String title = doc.select("span.B_NuCI").text();

			// Extract price
			String price = doc.select("div._30jeq3._16Jk6d").text();

			// Extract description
			String description = doc.select("div._1mXcCf").text();

			// Extract highlights (bullet points)
			Elements highlights = doc.select("li._21Ahn-");
			StringBuilder highlightList = new StringBuilder();
			for (Element li : highlights) {
				highlightList.append("- ").append(li.text()).append("\n");
			}

			// Extract image URLs
			Elements imageElements = doc.select("img._396cs4._2amPTt._3qGmMb");
			StringBuilder imageUrls = new StringBuilder();
			for (Element img : imageElements) {
				imageUrls.append(img.attr("src")).append("\n");
			}

			// Print extracted details
			System.out.println("Product Title: " + title);
			System.out.println("Price: " + price);
			System.out.println("Description: " + description);
			System.out.println("Highlights:\n" + highlightList);
			System.out.println("Image URLs:\n" + imageUrls);

			// Step 2: Prepare JSON for Shopify
			String jsonPayload = "{ \"product\": {" + "\"title\": \"" + title + "\"," + "\"body_html\": \""
					+ description + "\"," + "\"variants\": [{ \"price\": \"" + price.replaceAll("[^0-9.]", "") + "\"}]"
					+ "} }";

			// Step 3: Upload to Shopify
			String shopifyUrl = "https://yourstore.myshopify.com/admin/api/2023-07/products.json";
			String apiKey = "YOUR_API_KEY";
			String password = "YOUR_API_PASSWORD";

			URL url = new URL(shopifyUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			String auth = apiKey + ":" + password;
			String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
			conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
			conn.setDoOutput(true);

			try (OutputStream os = conn.getOutputStream()) {
				os.write(jsonPayload.getBytes());
			}

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void disableSSLVerification() {

		try {
			// Trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
					// Do nothing
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
					// Do nothing
				}
			} };

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = (hostname, session) -> true;
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			System.out.println("SSL verification disabled.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
