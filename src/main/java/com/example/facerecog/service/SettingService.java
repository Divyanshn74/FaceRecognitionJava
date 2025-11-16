package com.example.facerecog.service;

import com.example.facerecog.model.Setting;
import com.example.facerecog.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    @Value("${face.engine.url}")
    private String defaultFaceEngineUrl;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Cacheable("settings")
    public String getSetting(String key) {
        return settingRepository.findBySettingKey(key)
                .map(Setting::getSettingValue)
                .orElseGet(() -> {
                    if ("face.engine.url".equals(key)) {
                        return defaultFaceEngineUrl;
                    }
                    return null;
                });
    }

    @Transactional
    @CacheEvict(value = "settings", allEntries = true)
    public void updateSetting(String key, String value) {
        Setting setting = settingRepository.findBySettingKey(key)
                .orElse(new Setting(key, value));
        setting.setSettingValue(value);
        settingRepository.save(setting);
    }

    public Map<String, String> getAllSettings() {
        // This provides a view of all settings, useful for the settings page
        Map<String, String> allSettings = settingRepository.findAll().stream()
                .collect(Collectors.toMap(Setting::getSettingKey, Setting::getSettingValue));
        
        // Ensure the default python url is present if not in the DB
        allSettings.putIfAbsent("face.engine.url", defaultFaceEngineUrl);
        
        return allSettings;
    }
}
