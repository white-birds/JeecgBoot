package org.aihom.modules.superset.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.aihom.modules.superset.mapper.SupersetMetadataMapper;
import org.aihom.modules.superset.service.ISupersetService;
import org.aihom.modules.superset.vo.SupersetDashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SupersetServiceImpl implements ISupersetService {
    @Autowired
    private SupersetMetadataMapper metadataMapper;
    @Override
    @DS("superset") // 关键：指定这个方法走 SQLite 库
    public List<SupersetDashboardVO> getDashboardList() {
        log.info("开始查询 Superset 仪表板列表...");
        List<Map<String, Object>> rawData = metadataMapper.selectEmbeddedDashboards();
        log.info("从数据库查询到 {} 条记录", rawData.size());
        
        List<SupersetDashboardVO> result = new ArrayList<>();

        for (Map<String, Object> map : rawData) {
            String name = (String) map.get("name");
            String rawUuid = (String) map.get("rawUuid");
            
            log.info("原始数据 - name: {}, rawUuid: {}", name, rawUuid);

            // 将 32 位十六进制字符串转换为 8-4-4-4-12 格式
            String formattedUuid = formatUuid(rawUuid);
            log.info("格式化后的 UUID: {}", formattedUuid);
            
            result.add(new SupersetDashboardVO(name, formattedUuid));
        }
        
        log.info("最终返回 {} 个仪表板", result.size());
        return result;
    }

    private String formatUuid(String hex) {
        if (hex == null || hex.length() != 32) return hex;
        return hex.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        );
    }
    @Override
    public String getGuestTokenForEmbeddedDashboard(String embeddedDashboardUuid) {
        log.info("========== 开始获取 Embedded Dashboard Guest Token ==========");
        log.info("请求的 embeddedDashboardUuid: {}", embeddedDashboardUuid);
        
        if (embeddedDashboardUuid == null || embeddedDashboardUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("embeddedDashboardUuid 不能为空");
        }

        String accessToken = loginAndGetAccessToken();
        String csrfToken = getCsrfToken(accessToken);
        String url = supersetUrl + "/api/v1/security/guest_token/";

        log.info("Superset URL: {}", url);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("resources", List.of(
                Map.of("type", "dashboard", "id", embeddedDashboardUuid)
        ));
        requestBody.put("rls", List.of());

        // ✅ 关键改动 1：显式指定角色为 Public
        // 这会强制让 Embedded 模式使用 Public 权限，而不是你主系统的 Admin 权限
        requestBody.put("roles", List.of("Public"));

        requestBody.put("user", Map.of(
                "username", "guest_user", // 建议改成 guest_user 区分一下
                "first_name", "Guest",
                "last_name", "User",
                "locale", "zh"
        ));
        String jsonBody = JSON.toJSONString(requestBody);
        log.info("请求体: {}", jsonBody);

        RequestBody body = RequestBody.create(jsonBody, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("X-CSRFToken", csrfToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            int statusCode = response.code();
            String responseBody = response.body() != null ? response.body().string() : "无响应体";
            
            log.info("Superset 响应状态码: {}", statusCode);
            log.info("Superset 响应内容: {}", responseBody);
            
            if (!response.isSuccessful()) {
                log.error("Superset guest_token(embedded) 请求失败，状态码: {}, 响应: {}", statusCode, responseBody);
                throw new RuntimeException("Superset API 调用失败: " + statusCode + ", body=" + responseBody);
            }

            JSONObject jsonResponse = JSON.parseObject(responseBody);
            String token = jsonResponse.getString("token");
            if (token == null || token.trim().isEmpty()) {
                log.error("Superset guest_token(embedded) 响应缺少 token 字段，响应: {}", responseBody);
                throw new RuntimeException("获取 token 失败：响应中没有 token");
            }
            
            log.info("成功获取 guest token: {}", token.substring(0, Math.min(20, token.length())) + "...");
            log.info("========== Guest Token 获取完成 ==========");
            return token;
        } catch (IOException e) {
            log.error("Superset guest_token(embedded) 网络请求失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取系统访问令牌失败: " + e.getMessage());
        }
    }

    @Value("${superset.url:http://localhost:8088}")
    private String supersetUrl;

    @Value("${superset.auth.username:admin}")
    private String supersetUsername;

    @Value("${superset.auth.password:admin}")
    private String supersetPassword;

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static final CookieJar cookieJar = new CookieJar() {
        private final Map<String, List<Cookie>> store = new ConcurrentHashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            store.put(url.host(), cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            return store.getOrDefault(url.host(), new ArrayList<>());
        }
    };

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public String getGuestTokenForDashboard(String dashboardId) {
        if (dashboardId == null || dashboardId.trim().isEmpty()) {
            throw new IllegalArgumentException("dashboardId 不能为空");
        }

        String accessToken = loginAndGetAccessToken();
        String csrfToken = getCsrfToken(accessToken);
        String url = supersetUrl + "/api/v1/security/guest_token/";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("resources", List.of(
                Map.of("type", "dashboard", "id", dashboardId)
        ));
        requestBody.put("rls", List.of());
        requestBody.put("roles", List.of("Public"));
        requestBody.put("user", Map.of(
                "username", "guest",
                "first_name", "Guest",
                "last_name", "User",
                "locale", "zh"  // 设置语言为中文
        ));

        String jsonBody = JSON.toJSONString(requestBody);

        RequestBody body = RequestBody.create(jsonBody, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("X-CSRFToken", csrfToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                log.error("Superset guest_token 请求失败，状态码: {}, 响应: {}", response.code(), errorBody);
                throw new RuntimeException("Superset API 调用失败: " + response.code() + ", body=" + errorBody);
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            JSONObject jsonResponse = JSON.parseObject(responseBody);
            String token = jsonResponse.getString("token");
            if (token == null || token.trim().isEmpty()) {
                log.error("Superset guest_token 响应缺少 token 字段，响应: {}", responseBody);
                throw new RuntimeException("获取 token 失败：响应中没有 token");
            }
            return token;
        } catch (IOException e) {
            log.error("Superset guest_token 网络请求失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取系统访问令牌失败: " + e.getMessage());
        }
    }

    private String loginAndGetAccessToken() {
        if (supersetUsername == null || supersetUsername.trim().isEmpty()) {
            throw new IllegalStateException("superset.auth.username 未配置");
        }
        if (supersetPassword == null || supersetPassword.trim().isEmpty()) {
            throw new IllegalStateException("superset.auth.password 未配置");
        }

        String url = supersetUrl + "/api/v1/security/login";

        Map<String, Object> loginBody = new HashMap<>();
        loginBody.put("username", supersetUsername);
        loginBody.put("password", supersetPassword);
        loginBody.put("provider", "db");
        loginBody.put("refresh", true);

        RequestBody body = RequestBody.create(JSON.toJSONString(loginBody), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                log.error("Superset login 失败，状态码: {}, 响应: {}", response.code(), errorBody);
                throw new RuntimeException("Superset login 失败: " + response.code() + ", body=" + errorBody);
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            JSONObject json = JSON.parseObject(responseBody);
            String accessToken = json.getString("access_token");
            if (accessToken == null || accessToken.trim().isEmpty()) {
                log.error("Superset login 响应缺少 access_token，响应: {}", responseBody);
                throw new RuntimeException("Superset login 响应缺少 access_token，body=" + responseBody);
            }
            return accessToken;
        } catch (IOException e) {
            log.error("Superset login 网络请求失败: {}", e.getMessage(), e);
            throw new RuntimeException("Superset login 网络请求失败: " + e.getMessage());
        }
    }

    private String getCsrfToken(String accessToken) {
        String url = supersetUrl + "/api/v1/security/csrf_token/";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                log.error("Superset csrf_token 获取失败，状态码: {}, 响应: {}", response.code(), errorBody);
                throw new RuntimeException("Superset csrf_token 获取失败: " + response.code() + ", body=" + errorBody);
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            JSONObject json = JSON.parseObject(responseBody);
            String csrfToken = json.getString("result");
            if (csrfToken == null || csrfToken.trim().isEmpty()) {
                log.error("Superset csrf_token 响应缺少 result 字段，响应: {}", responseBody);
                throw new RuntimeException("Superset csrf_token 响应缺少 result，body=" + responseBody);
            }
            return csrfToken;
        } catch (IOException e) {
            log.error("Superset csrf_token 网络请求失败: {}", e.getMessage(), e);
            throw new RuntimeException("Superset csrf_token 网络请求失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("supersetUrl", supersetUrl);
        return config;
    }
}

