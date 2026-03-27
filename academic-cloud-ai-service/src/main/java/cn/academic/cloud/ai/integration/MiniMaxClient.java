package cn.academic.cloud.ai.integration;

import cn.academic.cloud.ai.config.AiModelProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class MiniMaxClient {

    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final AiModelProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MiniMaxClient(AiModelProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(8000);
        factory.setReadTimeout(20000);
        this.restTemplate = new RestTemplate(factory);
    }

    public String chat(String prompt) {
        return chat(prompt, 0.3d, properties.getMaxTokens() == null ? 1024 : properties.getMaxTokens());
    }

    public String chat(String prompt, double temperature, int maxTokens) {
        if (!properties.isReady() || !StringUtils.hasText(prompt)) {
            return null;
        }

        String url = buildUrl(properties.getBaseUrl(), properties.getChatPath());
        HttpHeaders headers = buildJsonHeaders();
        Map<String, Object> body = buildChatBody(prompt, temperature, maxTokens, false);

        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);
            return parseContent(response.getBody());
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean streamChat(String prompt, double temperature, int maxTokens, Consumer<String> onChunk) {
        if (!properties.isReady() || !StringUtils.hasText(prompt) || onChunk == null) {
            return false;
        }
        String url = buildUrl(properties.getBaseUrl(), properties.getChatPath());
        Map<String, Object> body = buildChatBody(prompt, temperature, maxTokens, true);
        String bodyJson = toJson(body);
        if (!StringUtils.hasText(bodyJson)) {
            return false;
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(65))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .header("x-api-key", properties.getApiKey())
                .header("anthropic-version", ANTHROPIC_VERSION);
        if (StringUtils.hasText(properties.getGroupId())) {
            requestBuilder.header("group-id", properties.getGroupId());
            requestBuilder.header("x-group-id", properties.getGroupId());
        }
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() < 200 || response.statusCode() >= 300 || response.body() == null) {
                return false;
            }
            return readStreamChunks(response.body(), onChunk);
        } catch (Exception ex) {
            return false;
        }
    }

    public String getModelName() {
        return properties.getModel();
    }

    public boolean isEnabled() {
        return properties.isReady();
    }

    private HttpHeaders buildJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(properties.getApiKey());
        headers.set("x-api-key", properties.getApiKey());
        if (StringUtils.hasText(properties.getGroupId())) {
            headers.set("group-id", properties.getGroupId());
            headers.set("x-group-id", properties.getGroupId());
        }
        headers.set("anthropic-version", ANTHROPIC_VERSION);
        return headers;
    }

    private Map<String, Object> buildChatBody(String prompt, double temperature, int maxTokens, boolean stream) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", properties.getModel());
        body.put("max_tokens", Math.max(maxTokens, 64));
        body.put("temperature", temperature);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        if (stream) {
            body.put("stream", true);
        }
        return body;
    }

    private String toJson(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean readStreamChunks(InputStream stream, Consumer<String> onChunk) {
        boolean hasChunk = false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("data:")) {
                    continue;
                }
                String payload = line.substring(5).trim();
                if (!StringUtils.hasText(payload)) {
                    continue;
                }
                if ("[DONE]".equals(payload)) {
                    break;
                }
                String delta = parseStreamDelta(payload);
                if (StringUtils.hasText(delta)) {
                    onChunk.accept(delta);
                    hasChunk = true;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return hasChunk;
    }

    private String parseStreamDelta(String payload) {
        try {
            JsonNode body = objectMapper.readTree(payload);
            if (body == null) {
                return null;
            }

            String type = body.path("type").asText();
            if ("content_block_delta".equals(type) && body.path("delta").hasNonNull("text")) {
                return body.path("delta").get("text").asText();
            }
            if ("content_block_start".equals(type) && body.path("content_block").hasNonNull("text")) {
                return body.path("content_block").get("text").asText();
            }

            JsonNode choices = body.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode first = choices.get(0);
                JsonNode deltaNode = first.path("delta");
                if (deltaNode.hasNonNull("content")) {
                    return deltaNode.get("content").asText();
                }
                JsonNode deltaContent = deltaNode.path("content");
                if (deltaContent.isArray() && !deltaContent.isEmpty()) {
                    JsonNode contentItem = deltaContent.get(0);
                    if (contentItem.hasNonNull("text")) {
                        return contentItem.get("text").asText();
                    }
                }
            }

            JsonNode content = body.path("content");
            if (content.isArray() && !content.isEmpty()) {
                JsonNode first = content.get(0);
                if (first.hasNonNull("text")) {
                    return first.get("text").asText();
                }
                if (first.isTextual()) {
                    return first.asText();
                }
            }

            if (body.hasNonNull("output_text")) {
                return body.get("output_text").asText();
            }
            if (body.hasNonNull("reply")) {
                return body.get("reply").asText();
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    private String parseContent(JsonNode body) {
        if (body == null) {
            return null;
        }

        JsonNode content = body.path("content");
        if (content.isArray() && !content.isEmpty()) {
            JsonNode first = content.get(0);
            if (first != null) {
                if (first.hasNonNull("text")) {
                    return first.get("text").asText().trim();
                }
                if (first.isTextual()) {
                    return first.asText().trim();
                }
            }
        }

        JsonNode choices = body.path("choices");
        if (choices.isArray() && !choices.isEmpty()) {
            JsonNode first = choices.get(0);
            JsonNode message = first.path("message");
            if (message.hasNonNull("content")) {
                return message.get("content").asText().trim();
            }
            if (first.hasNonNull("text")) {
                return first.get("text").asText().trim();
            }
        }

        if (body.hasNonNull("output_text")) {
            return body.get("output_text").asText().trim();
        }
        if (body.hasNonNull("reply")) {
            return body.get("reply").asText().trim();
        }
        return null;
    }

    private String buildUrl(String baseUrl, String path) {
        String cleanBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String cleanPath = path.startsWith("/") ? path : "/" + path;
        return cleanBase + cleanPath;
    }
}
