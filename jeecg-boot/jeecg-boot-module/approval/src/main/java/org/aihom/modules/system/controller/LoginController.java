package org.aihom.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

/**
 * 用户登录与注册控制器 (采用 JWT 单点登录，已剥离 Keycloak)
 * @author AIHOM
 */
@RestController
@RequestMapping("/sys")
@Tag(name = "用户登录")
@Slf4j
public class LoginController {

    // ⚠️ 极其重要：这是你和 Superset 约定的共同秘钥，两边必须一模一样！
    private static final String SSO_SECRET = "aihom_superset_secret_key_2026";

    /**
     * 封装一个生成 JWT 的私有方法，供登录和注册复用
     */
    private String generateSsoToken(String username, String realname) {
        Algorithm algorithm = Algorithm.HMAC256(SSO_SECRET);
        return JWT.create()
                .withClaim("username", username)
                .withClaim("realname", realname) // 把真实姓名也带过去，Superset创建账号时要用
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 3600 * 1000)) // 24小时过期
                .sign(algorithm);
    }

    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public Result<JSONObject> login(@RequestBody JSONObject loginModel) {
        Result<JSONObject> result = new Result<>();
        String username = loginModel.getString("username");
        String password = loginModel.getString("password");

        log.info("用户登录: username={}", username);

        // TODO: 替换为你实际的查库验证逻辑
        if ("admin".equals(username) && "admin".equals(password)) {
            String realname = "管理员"; // 实际应该从数据库查出来

            JSONObject userInfo = new JSONObject();
            userInfo.put("id", "1");
            userInfo.put("username", username);
            userInfo.put("realname", realname);
            userInfo.put("avatar", "");

            JSONObject data = new JSONObject();
            // 核心：签发 JWT Token
            data.put("token", generateSsoToken(username, realname));
            data.put("userInfo", userInfo);

            result.setResult(data);
            result.setSuccess(true);
            result.setMessage("登录成功");

            log.info("用户登录成功: username={}", username);
        } else {
            result.setSuccess(false);
            result.setMessage("用户名或密码错误");
            log.warn("用户登录失败: username={}", username);
        }

        return result;
    }

    @Operation(summary = "注册接口")
    @PostMapping("/register")
    public Result<JSONObject> register(@RequestBody JSONObject registerModel) {
        Result<JSONObject> result = new Result<>();
        String username = registerModel.getString("username");
        String password = registerModel.getString("password");
        String realname = registerModel.getString("realname");

        // TODO: 1. 这里执行你主系统数据库的 INSERT 插入用户操作

        // 2. 注册成功后，直接让他处于登录状态（颁发 Token）
        JSONObject userInfo = new JSONObject();
        userInfo.put("id", UUID.randomUUID().toString());
        userInfo.put("username", username);
        userInfo.put("realname", realname == null ? username : realname);

        JSONObject data = new JSONObject();
        // 关键：注册完也发一个 JWT，等他带着这个 Token 访问看板时，Superset 会自动同步创建账号！
        data.put("token", generateSsoToken(username, userInfo.getString("realname")));
        data.put("userInfo", userInfo);

        result.setResult(data);
        result.setSuccess(true);
        result.setMessage("注册并登录成功");
        return result;
    }

    // ... logout 和 getUserInfo 保持你原来的不变

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Object> logout() {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        result.setMessage("退出成功");
        return result;
    }
    
    @Operation(summary = "获取用户信息")
    @GetMapping("/user/getUserInfo")
    public Result<JSONObject> getUserInfo() {
        Result<JSONObject> result = new Result<>();
        
        JSONObject userInfo = new JSONObject();
        userInfo.put("id", "1");
        userInfo.put("username", "admin");
        userInfo.put("realname", "管理员");
        userInfo.put("avatar", "");
        
        result.setResult(userInfo);
        result.setSuccess(true);
        
        return result;
    }
}
