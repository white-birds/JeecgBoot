package org.aihom.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.aihom.modules.system.entity.ApprovalUser;
import org.aihom.modules.system.service.IApprovalUserService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

    @Autowired
    private IApprovalUserService userService;

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

        // 从数据库查找用户
        ApprovalUser user = userService.getUserByUsername(username);
        
        if (user == null) {
            result.setSuccess(false);
            result.setMessage("用户不存在");
            log.warn("用户登录失败，用户不存在: username={}", username);
            return result;
        }
        
        // 验证密码（使用 JeecgBoot 的加密方式）
        String encryptedPassword = PasswordUtil.encrypt(username, password, user.getSalt());
        
        if (encryptedPassword.equals(user.getPassword())) {
            // 检查用户状态
            if (user.getStatus() == 0) {
                result.setSuccess(false);
                result.setMessage("账号已被冻结，请联系管理员");
                log.warn("用户登录失败，账号已冻结: username={}", username);
                return result;
            }
            
            // 检查删除标志
            if (user.getDelFlag() != null && user.getDelFlag() == 1) {
                result.setSuccess(false);
                result.setMessage("账号已被删除");
                log.warn("用户登录失败，账号已删除: username={}", username);
                return result;
            }
            
            String realname = user.getRealname();

            JSONObject userInfo = new JSONObject();
            userInfo.put("id", user.getId());
            userInfo.put("username", username);
            userInfo.put("realname", realname);
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("avatar", user.getAvatar() == null ? "" : user.getAvatar());

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
            log.warn("用户登录失败，密码错误: username={}", username);
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
        String email = registerModel.getString("email");
        String phone = registerModel.getString("phone");

        log.info("用户注册: username={}", username);

        // 1. 验证用户名不为空
        if (username == null || username.trim().isEmpty()) {
            result.setSuccess(false);
            result.setMessage("用户名不能为空");
            log.warn("注册失败，用户名为空");
            return result;
        }

        // 2. 验证用户名是否已存在
        ApprovalUser existUser = userService.getUserByUsername(username);
        if (existUser != null) {
            result.setSuccess(false);
            result.setMessage("用户名已存在");
            log.warn("注册失败，用户名已存在: username={}", username);
            return result;
        }

        // 3. 验证密码不为空
        if (password == null || password.trim().isEmpty()) {
            result.setSuccess(false);
            result.setMessage("密码不能为空");
            log.warn("注册失败，密码为空");
            return result;
        }

        // 4. 创建新用户
        ApprovalUser newUser = new ApprovalUser();
        
        // 生成盐值
        String salt = oConvertUtils.randomGen(8);
        // 加密密码
        String encryptedPassword = PasswordUtil.encrypt(username, password, salt);
        
        newUser.setUsername(username.trim());
        newUser.setPassword(encryptedPassword);
        newUser.setSalt(salt);
        newUser.setRealname((realname == null || realname.trim().isEmpty()) ? username : realname.trim());
        newUser.setEmail(email == null ? "" : email.trim());
        newUser.setPhone(phone == null ? "" : phone.trim());
        newUser.setStatus(1); // 正常状态
        newUser.setDelFlag(0); // 未删除

        // 5. 保存到数据库
        try {
            userService.registerUser(newUser);
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("注册失败，请稍后重试");
            return result;
        }

        // 6. 注册成功后，直接让他处于登录状态（颁发 Token）
        JSONObject userInfo = new JSONObject();
        userInfo.put("id", newUser.getId());
        userInfo.put("username", username);
        userInfo.put("realname", newUser.getRealname());
        userInfo.put("email", newUser.getEmail());
        userInfo.put("phone", newUser.getPhone());
        userInfo.put("avatar", "");

        JSONObject data = new JSONObject();
        // 关键：注册完也发一个 JWT，等他带着这个 Token 访问看板时，Superset 会自动同步创建账号！
        data.put("token", generateSsoToken(username, newUser.getRealname()));
        data.put("userInfo", userInfo);

        result.setResult(data);
        result.setSuccess(true);
        result.setMessage("注册成功");
        
        log.info("用户注册成功: username={}, realname={}", username, newUser.getRealname());
        
        return result;
    }

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
    public Result<JSONObject> getUserInfo(@RequestHeader(value = "X-Access-Token", required = false) String token) {
        Result<JSONObject> result = new Result<>();
        
        try {
            // 1. 验证 Token 是否存在
            if (token == null || token.trim().isEmpty()) {
                result.setSuccess(false);
                result.setMessage("未登录或登录已过期");
                result.setCode(401);
                return result;
            }
            
            // 2. 解析 Token 获取用户名
            Algorithm algorithm = Algorithm.HMAC256(SSO_SECRET);
            com.auth0.jwt.interfaces.DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            String username = jwt.getClaim("username").asString();
            
            // 3. 从数据库中获取用户信息
            ApprovalUser user = userService.getUserByUsername(username);
            
            if (user == null) {
                result.setSuccess(false);
                result.setMessage("用户不存在");
                result.setCode(404);
                return result;
            }
            
            // 4. 返回用户信息（不包含密码）
            JSONObject userInfo = new JSONObject();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realname", user.getRealname());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("avatar", user.getAvatar() == null ? "" : user.getAvatar());
            
            result.setResult(userInfo);
            result.setSuccess(true);
            result.setMessage("获取用户信息成功");
            
            log.info("获取用户信息成功: username={}", username);
            
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            result.setSuccess(false);
            result.setMessage("Token 无效或已过期");
            result.setCode(401);
        }
        
        return result;
    }
}
