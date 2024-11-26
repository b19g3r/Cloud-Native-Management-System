package com.windsurf.common.security.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@TableName("user")
public class User {

    @TableId
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Length(min = 4, max = 50, message = "用户名长度必须在4-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "用户名只能包含字母、数字、下划线和连字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    private Boolean enabled;

    private String roles;

    private UserStatus status;

    private LocalDateTime createTime;

    private LocalDateTime lastLoginTime;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String avatar;

    @Builder.Default
    private Integer loginAttempts = 0;

    private LocalDateTime lockTime;

    public boolean isAccountNonLocked() {
        return status != UserStatus.LOCKED || 
               (lockTime != null && lockTime.isBefore(LocalDateTime.now()));
    }

    public boolean isAccountNonExpired() {
        return status != UserStatus.INACTIVE;
    }

    public boolean isEnabled() {
        return enabled && status == UserStatus.ACTIVE;
    }

    public void incrementLoginAttempts() {
        this.loginAttempts = (this.loginAttempts == null ? 0 : this.loginAttempts) + 1;
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0;
        this.lockTime = null;
    }
}
