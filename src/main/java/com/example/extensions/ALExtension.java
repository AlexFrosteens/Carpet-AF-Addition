package com.example.extensions;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.utils.Translations;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

public class ALExtension implements CarpetExtension {
    private SettingsManager settingsManager;

    @Override
    public void onGameStarted() {
        carpet.utils.Translations.updateLanguage();
//        settingsManager = new SettingsManager("1.0", "carpetaladdition", "ALAddition");
//        settingsManager.parseSettingsClass(Settings.class);
        CarpetServer.settingsManager.parseSettingsClass(Settings.class);
    }

    @Override
    public SettingsManager extensionSettingsManager() {
        return settingsManager;
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {

    }

    @Override
    public Map<String, String> canHasTranslations(String language) {
        // 从 assets/carpetaladdition/lang/<language>.json 加载
        String path = String.format("assets/carpetaladdition/lang/%s.json", language);
        return Translations.getTranslationFromResourcePath(path);
    }

}
