package com.example.facerecog.service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class FaceEngineService {

    private final SettingService settingService;

    public FaceEngineService(SettingService settingService) {
        this.settingService = settingService;
    }

    public String getEmbedding(String base64Image) {
        try {
            String faceEngineBaseUrl = settingService.getSetting("face.engine.url");
            JSONObject requestBody = new JSONObject();
            requestBody.put("image", base64Image);

            HttpResponse<String> response = Unirest.post(faceEngineBaseUrl + "/get_embedding")
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .asString();

            if (response.isSuccess()) {
                return response.getBody();
            } else {
                System.err.println("Error getting embedding: " + response.getStatus() + " - " + response.getStatusText());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Exception while calling DeepFace /get_embedding: " + e.getMessage());
            return null;
        }
    }

    public boolean compare(String knownEmbedding, String liveBase64) {
        try {
            String faceEngineBaseUrl = settingService.getSetting("face.engine.url");
            JSONObject requestBody = new JSONObject();
            // Assuming knownEmbedding is a JSON string representation of a list/array
            // Unirest's JSONObject can parse this directly if it's valid JSON
            requestBody.put("known_embedding", new JSONObject(knownEmbedding).getJSONArray("embedding"));
            requestBody.put("unknown_image", liveBase64);

            HttpResponse<String> response = Unirest.post(faceEngineBaseUrl + "/compare")
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .asString();

            if (response.isSuccess()) {
                // Assuming the Flask service returns a JSON object like {"match": true/false}
                JSONObject jsonResponse = new JSONObject(response.getBody());
                return jsonResponse.getBoolean("match");
            } else {
                System.err.println("Error comparing faces: " + response.getStatus() + " - " + response.getStatusText());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception while calling DeepFace /compare: " + e.getMessage());
            return false;
        }
    }
}
