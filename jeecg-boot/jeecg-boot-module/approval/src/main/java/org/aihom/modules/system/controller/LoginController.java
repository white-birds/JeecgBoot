package org.aihom.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 简单登录控制器
 * @author AIHOM
 */
@RestController
@RequestMapping("/sys")
@Tag(name = "用户登录")
@Slf4j
public class LoginController {

    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public Result<JSONObject> login(@RequestBody JSONObject loginModel) {
        Result<JSONObject> result = new Result<>();
        String username = loginModel.getString("username");
        String password = loginModel.getString("password");
        
        log.info("用户登录: username={}", username);
        
        // 简单验证（实际项目中应该查询数据库验证）
        if ("admin".equals(username) && "admin".equals(password)) {
            JSONObject userInfo = new JSONObject();
            userInfo.put("id", "1");
            userInfo.put("username", username);
            userInfo.put("realname", "管理员");
            userInfo.put("avatar", "");
            
            JSONObject data = new JSONObject();
            data.put("token", UUID.randomUUID().toString().replace("-", ""));
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
