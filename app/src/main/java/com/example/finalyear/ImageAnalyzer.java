package com.example.finalyear;

import android.net.Uri;
import java.io.IOException;
import okhttp3.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



public class ImageAnalyzer {

    private final String apiKey = "pYZceGWk8EeqV1r7gYQP";
    private final OkHttpClient client = new OkHttpClient();
    private final String apiUrl = "https://detect.roboflow.com/detectorize/1";
    private final MediaType contentType = MediaType.parse("application/x-www-form-urlencoded");

    public interface AnalysisCallback {
        void onAnalysisSuccess(String imageUrl, String result);

        void onAnalysisFailure(String imageUrl, String errorMessage);
    }

    public void analyzeImage(String imageUrl, AnalysisCallback callback) {
        String urlWithParams = Uri.parse(apiUrl)
                .buildUpon()
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("image", imageUrl)
                .build().toString();

        RequestBody requestBody = RequestBody.create(contentType, "");

        Request request = new Request.Builder()
                .url(urlWithParams)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    ObjectMapper objectMapper = new ObjectMapper();

                    try {
                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        JsonNode predictionsNode = jsonNode.get("predictions");

                        if (predictionsNode != null && predictionsNode.isArray() && predictionsNode.size() > 0) {
                            JsonNode prediction = predictionsNode.get(0);
                            String predictedClass = prediction.get("class").asText();
                            double confidence = prediction.get("confidence").asDouble();

                            // You can now use the predictedClass and confidence as needed
                            System.out.println("Predicted Class: " + predictedClass);
                            System.out.println("Confidence: " + confidence);

                            // You may want to pass this information to the callback
                            callback.onAnalysisSuccess(imageUrl, "Predicted Class: " + predictedClass + ", Confidence: " + confidence);
                        } else {
                            callback.onAnalysisFailure(imageUrl, "No predictions found in the response");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onAnalysisFailure(imageUrl, "Error parsing JSON response");
                    }
                } else {
                    callback.onAnalysisFailure(imageUrl, "API request failed");
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onAnalysisFailure(imageUrl, "IOException occurred");
            }
        });
    }
}
