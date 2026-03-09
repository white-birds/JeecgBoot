package org.aihom.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统用户实体（复用 JeecgBoot 的 sys_user 表）
 * @author AIHOM
 */
@Data
@TableName("sys_user")
public class ApprovalUser implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 密码盐值
     */
    private String salt;
    
    /**
     * 真实姓名
     */
    private String realname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 状态(1-正常,0-冻结)
     */
    private Integer status;
    
    /**
     * 删除标志(0-正常,1-已删除)
     */
    private Integer delFlag;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
