package com.doanchuyennganh.duong.service;

import com.doanchuyennganh.duong.dto.SettingsDTO;
import com.doanchuyennganh.duong.exception.CustomException;
import com.doanchuyennganh.duong.exception.GlobalExceptionHandler;
import com.doanchuyennganh.duong.model.UserSettings;
import com.doanchuyennganh.duong.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsService {

    private final UserSettingsRepository settingsRepository;

    public UserSettingsService(UserSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    /**
     * Lấy settings của user, tạo mới nếu chưa có
     */
    public UserSettings getUserSettings(Integer userId) {
        return settingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));
    }

    /**
     * Tạo settings mặc định cho user mới
     */
    @Transactional
    public UserSettings createDefaultSettings(Integer userId) {
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        settings.setNotificationEnabled(true);
        settings.setSoundEnabled(true);
        settings.setDarkModeEnabled(false);
        settings.setAutoPlayEnabled(false);
        settings.setFontSize(16);
        settings.setDailyGoal(10);

        return settingsRepository.save(settings);
    }

    /**
     * Cập nhật settings
     */
    @Transactional
    public UserSettings updateSettings(Integer userId, SettingsDTO settingsDTO) {
        // Validate settings
        validateSettings(settingsDTO);

        UserSettings settings = getUserSettings(userId);

        if (settingsDTO.getNotificationEnabled() != null) {
            settings.setNotificationEnabled(settingsDTO.getNotificationEnabled());
        }
        if (settingsDTO.getSoundEnabled() != null) {
            settings.setSoundEnabled(settingsDTO.getSoundEnabled());
        }
        if (settingsDTO.getDarkModeEnabled() != null) {
            settings.setDarkModeEnabled(settingsDTO.getDarkModeEnabled());
        }
        if (settingsDTO.getAutoPlayEnabled() != null) {
            settings.setAutoPlayEnabled(settingsDTO.getAutoPlayEnabled());
        }
        if (settingsDTO.getFontSize() != null) {
            settings.setFontSize(settingsDTO.getFontSize());
        }
        if (settingsDTO.getDailyGoal() != null) {
            settings.setDailyGoal(settingsDTO.getDailyGoal());
        }

        return settingsRepository.save(settings);
    }

    /**
     * Validate settings data
     */
    private void validateSettings(SettingsDTO settingsDTO) {
        if (settingsDTO.getFontSize() != null) {
            if (settingsDTO.getFontSize() < 12 || settingsDTO.getFontSize() > 24) {
                throw new CustomException.InvalidSettingsException("Font size must be between 12 and 24");
            }
        }

        if (settingsDTO.getDailyGoal() != null) {
            if (settingsDTO.getDailyGoal() < 1 || settingsDTO.getDailyGoal() > 50) {
                throw new CustomException.InvalidSettingsException("Daily goal must be between 1 and 50");
            }
        }
    }

    /**
     * Reset settings về mặc định
     */
    @Transactional
    public UserSettings resetSettings(Integer userId) {
        UserSettings settings = getUserSettings(userId);

        settings.setNotificationEnabled(true);
        settings.setSoundEnabled(true);
        settings.setDarkModeEnabled(false);
        settings.setAutoPlayEnabled(false);
        settings.setFontSize(16);
        settings.setDailyGoal(10);

        return settingsRepository.save(settings);
    }

    /**
     * Lấy daily goal của user
     */
    public Integer getDailyGoal(Integer userId) {
        UserSettings settings = getUserSettings(userId);
        return settings.getDailyGoal();
    }
}
