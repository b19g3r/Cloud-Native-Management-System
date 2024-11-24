package com.windsurf.common.security.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    
    @TableId
    private Long id;
    
    private String username;
    
    private String password;
    
    private Boolean enabled;
    
    // 用户角色，使用逗号分隔的字符串存储，如："ROLE_USER,ROLE_ADMIN"
    private String roles;
}
