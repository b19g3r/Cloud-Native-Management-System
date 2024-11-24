package com.windsurf.common.security.mapper;

import com.windsurf.common.security.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.windsurf.common.security.SecurityTestApplication;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SecurityTestApplication.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @Sql(scripts = "/sql/init_user.sql")
    void testSelectByUsername() {
        // 通过用户名查询
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "testuser")
        );

        // 验证查询结果
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertTrue(user.getEnabled());
        assertEquals("ROLE_USER", user.getRoles());
    }

    @Test
    @Sql(scripts = "/sql/init_user.sql")
    void testSelectByUsernameNotFound() {
        // 查询不存在的用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "nonexistent")
        );

        // 验证查询结果为空
        assertNull(user);
    }

    @Test
    @Sql(scripts = "/sql/init_user.sql")
    void testInsertUser() {
        // 创建新用户
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setEnabled(true);
        newUser.setRoles("ROLE_USER");

        // 插入用户
        int result = userMapper.insert(newUser);

        // 验证插入结果
        assertEquals(1, result);
        assertNotNull(newUser.getId());

        // 验证能够查询到插入的用户
        User savedUser = userMapper.selectById(newUser.getId());
        assertNotNull(savedUser);
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("password123", savedUser.getPassword());
        assertTrue(savedUser.getEnabled());
        assertEquals("ROLE_USER", savedUser.getRoles());
    }

    @Test
    @Sql(scripts = "/sql/init_user.sql")
    void testUpdateUser() {
        // 先查询已存在的用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "testuser")
        );
        assertNotNull(user);

        // 修改用户信息
        user.setRoles("ROLE_USER,ROLE_ADMIN");
        user.setEnabled(false);

        // 更新用户
        int result = userMapper.updateById(user);

        // 验证更新结果
        assertEquals(1, result);

        // 验证更新后的信息
        User updatedUser = userMapper.selectById(user.getId());
        assertNotNull(updatedUser);
        assertEquals("ROLE_USER,ROLE_ADMIN", updatedUser.getRoles());
        assertFalse(updatedUser.getEnabled());
    }

    @Test
    @Sql(scripts = "/sql/init_user.sql")
    void testDeleteUser() {
        // 先查询已存在的用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "testuser")
        );
        assertNotNull(user);

        // 删除用户
        int result = userMapper.deleteById(user.getId());

        // 验证删除结果
        assertEquals(1, result);

        // 验证用户已被删除
        User deletedUser = userMapper.selectById(user.getId());
        assertNull(deletedUser);
    }
}
