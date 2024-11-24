package com.windsurf.common.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.windsurf.common.security.entity.User;
import com.windsurf.common.security.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEnabled(true);
        testUser.setRoles("ROLE_USER,ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // 配置Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // 执行测试
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // 验证结果
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ShouldThrowException() {
        // 配置Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername("nonexistent")
        );
    }

    @Test
    void loadUserByUsername_WhenUserDisabled_ShouldThrowException() {
        // 准备禁用的用户数据
        testUser.setEnabled(false);
        
        // 配置Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // 执行测试并验证异常
        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername("testuser")
        );
    }

    @Test
    void loadUserByUsername_WhenUserHasNoRoles_ShouldReturnEmptyAuthorities() {
        // 准备没有角色的用户数据
        testUser.setRoles(null);
        
        // 配置Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // 执行测试
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // 验证结果
        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_WhenUserHasEmptyRoles_ShouldReturnEmptyAuthorities() {
        // 准备空角色的用户数据
        testUser.setRoles("");
        
        // 配置Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // 执行测试
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // 验证结果
        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().isEmpty());
    }
}
