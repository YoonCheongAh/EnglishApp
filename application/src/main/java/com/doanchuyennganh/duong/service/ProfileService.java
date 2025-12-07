package com.doanchuyennganh.duong.service;

import com.doanchuyennganh.duong.model.User;
import com.doanchuyennganh.duong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    private final String UPLOAD_DIR = "uploads/avatars/";

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public String saveAvatar(String base64, Long userId) {
        try {
            // Loại bỏ khoảng trắng, xuống dòng
            String cleanBase64 = base64.replaceAll("\\s+", "");

            // Loại bỏ prefix nếu có (data:image/jpeg;base64,)
            if (cleanBase64.contains(",")) {
                cleanBase64 = cleanBase64.split(",")[1];
            }

            System.out.println("=== SAVING AVATAR ===");
            System.out.println("User ID: " + userId);
            System.out.println("Base64 length (after clean): " + cleanBase64.length());

            // Decode base64
            byte[] imageBytes = java.util.Base64.getDecoder().decode(cleanBase64);
            System.out.println("Image bytes length: " + imageBytes.length);

            // Tạo thư mục nếu chưa có
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println("Directory created: " + created);
                System.out.println("Directory path: " + dir.getAbsolutePath());
            }

            // Tạo tên file
            String fileName = "avatar_" + userId + "_" + System.currentTimeMillis() + ".jpg";
            File file = new File(UPLOAD_DIR + fileName);

            System.out.println("Saving to: " + file.getAbsolutePath());

            // Ghi file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(imageBytes);
                fos.flush();
            }

            System.out.println("File saved successfully!");
            System.out.println("File size: " + file.length() + " bytes");

            // Trả về URL
            String url = "/uploads/avatars/" + fileName;
            System.out.println("Avatar URL: " + url);

            return url;

        } catch (IllegalArgumentException e) {
            System.err.println("Base64 decode error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid Base64 format", e);
        } catch (Exception e) {
            System.err.println("File save error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lưu avatar: " + e.getMessage(), e);
        }
    }

    public void updateAvatarUrl(Long userId, String url) {
        userRepository.updateAvatar(userId, url);
    }

    public User getProfile(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public void updateProfile(Long userId, Map<String, String> data) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (data.containsKey("fullname")) {
            user.setFullname(data.get("fullname"));
        }
        if (data.containsKey("username")) {
            String newUsername = data.get("username");
            if (!newUsername.equals(user.getUsername()) &&
                    userRepository.existsByUsername(newUsername)) {
                throw new RuntimeException("Tên đăng nhập đã tồn tại");
            }
            user.setUsername(newUsername);
        }
        if (data.containsKey("email")) {
            String newEmail = data.get("email");
            if (!newEmail.isEmpty() && !newEmail.equals(user.getEmail()) &&
                    userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email đã được sử dụng");
            }
            user.setEmail(newEmail);
        }

        userRepository.save(user);
    }
}