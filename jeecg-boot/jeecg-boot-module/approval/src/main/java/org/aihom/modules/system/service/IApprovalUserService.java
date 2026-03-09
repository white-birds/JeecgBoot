package org.aihom.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.aihom.modules.system.entity.ApprovalUser;

/**
 * 审批系统用户Service
 * @author AIHOM
 */
public interface IApprovalUserService extends IService<ApprovalUser> {
    
    /**
     * 根据用户名查询用户
     */
    ApprovalUser getUserByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     */
    ApprovalUser getUserByEmail(String email);
    
    /**
     * 根据手机号查询用户
     */
    ApprovalUser getUserByPhone(String phone);
    
    /**
     * 注册新用户
     */
    ApprovalUser registerUser(ApprovalUser user);
}
