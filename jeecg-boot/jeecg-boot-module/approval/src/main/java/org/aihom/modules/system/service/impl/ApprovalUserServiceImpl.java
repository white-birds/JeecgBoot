package org.aihom.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aihom.modules.system.entity.ApprovalUser;
import org.aihom.modules.system.mapper.ApprovalUserMapper;
import org.aihom.modules.system.service.IApprovalUserService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 审批系统用户Service实现
 * @author AIHOM
 */
@Service
@Slf4j
public class ApprovalUserServiceImpl extends ServiceImpl<ApprovalUserMapper, ApprovalUser> implements IApprovalUserService {
    
    @Override
    public ApprovalUser getUserByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }
    
    @Override
    public ApprovalUser getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return baseMapper.selectByEmail(email);
    }
    
    @Override
    public ApprovalUser getUserByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        return baseMapper.selectByPhone(phone);
    }
    
    @Override
    public ApprovalUser registerUser(ApprovalUser user) {
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setStatus(1); // 默认正常状态
        
        if (user.getRealname() == null || user.getRealname().trim().isEmpty()) {
            user.setRealname(user.getUsername());
        }
        
        baseMapper.insert(user);
        log.info("用户注册成功: username={}", user.getUsername());
        
        return user;
    }
}
