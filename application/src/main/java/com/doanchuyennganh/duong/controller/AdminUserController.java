package com.doanchuyennganh.duong.controller;

import com.doanchuyennganh.duong.dto.CreateUserRequest;
import com.doanchuyennganh.duong.dto.UpdateUserRequest;
import com.doanchuyennganh.duong.dto.UserResponse;
import com.doanchuyennganh.duong.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin API")
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả người dùng")
    public List<UserResponse> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy người dùng theo id")
    public UserResponse getUserById(@PathVariable Long id) {
        return adminUserService.getUserById(id);
    }

    @PostMapping
    @Operation(summary = "Tạo người dùng mới")
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return adminUserService.createUser(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Sửa thông tin người dùng")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return adminUserService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa người dùng")
    public void deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
    }
}