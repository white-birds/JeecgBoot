package org.aihom.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.aihom.modules.system.entity.ApprovalUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 审批系统用户Mapper
 * @author AIHOM
 */
@Mapper
public interface ApprovalUserMapper extends BaseMapper<ApprovalUser> {
    
    /**
     * 根据用户名查询用户
     */
    ApprovalUser selectByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     */
    ApprovalUser selectByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查询用户
     */
    ApprovalUser selectByPhone(@Param("phone") String phone);
}
