package com.example.housekeeping.controller;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.common.Result;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody @Valid User updateUser, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.updateUser(userId, updateUser);
        return Result.success("更新成功", user);
    }

    /**
     * 分页查询用户（管理员）
     */
    @GetMapping("/list")
    public Result<PageResult<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<User> userPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userService.searchUsers(keyword, pageable);
        } else if (status != null) {
            userPage = userService.findByStatus(status, pageable);
        } else {
            userPage = userService.findUsers(pageable);
        }
        
        return Result.success(PageResult.of(userPage));
    }

    /**
     * 根据ID获取用户信息（管理员）
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        return Result.success(user);
    }

    /**
     * 更新用户状态（管理员）
     */
    @PutMapping("/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        if (status == null) {
            return Result.badRequest("状态不能为空");
        }
        
        userService.updateUserStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 删除用户（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("用户删除成功");
    }

    /**
     * 获取用户统计信息（管理员）
     */
    @GetMapping("/statistics")
    public Result<Map<String, Long>> getUserStatistics() {
        Map<String, Long> statistics = Map.of(
            "totalUsers", userService.countUsers(),
            "activeUsers", userService.countActiveUsers()
        );
        return Result.success(statistics);
    }
}
