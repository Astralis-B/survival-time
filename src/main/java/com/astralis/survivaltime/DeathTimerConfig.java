package com.astralis.survivaltime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class DeathTimerConfig {

    // Serialization
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(
            FabricLoader.getInstance().getConfigDir().toFile(),
            "survival-time.json"
    );

    // Enums
    public enum Position {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, ACTION_BAR, CENTER_TOP
    }

    public enum TimeMode {
        REAL_TIME,      // Decimal real-world time  (e.g. 1.50 Hours)
        MINECRAFT_DAYS  // Decimal in-game time     (e.g. 2.75 Days)
    }

    // Config fields (defaults match fabric.mod.json description)
    public boolean enabled               = true;
    public Position position             = Position.CENTER_TOP;
    public TimeMode timeMode             = TimeMode.MINECRAFT_DAYS;
    public int offsetX                   = 0;
    public int offsetY                   = 0;
    public boolean deathAnimationEnabled = true;

    // Singleton
    private static DeathTimerConfig INSTANCE;

    public static DeathTimerConfig getInstance() {
        if (INSTANCE == null) return load();
        return INSTANCE;
    }

    // Persistence
    public static DeathTimerConfig load() {
        if (INSTANCE != null) return INSTANCE;

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, DeathTimerConfig.class);
            } catch (Exception e) {
                DeathTimerMod.LOGGER.error("Error loading config", e);
            }
        }

        if (INSTANCE == null) INSTANCE = new DeathTimerConfig();

        // Validate and clamp values in case the JSON file was edited manually
        if (INSTANCE.position == null) INSTANCE.position = Position.CENTER_TOP;
        if (INSTANCE.timeMode == null) INSTANCE.timeMode = TimeMode.MINECRAFT_DAYS;
        INSTANCE.offsetX = Math.max(-1000, Math.min(1000, INSTANCE.offsetX));
        INSTANCE.offsetY = Math.max(-1000, Math.min(1000, INSTANCE.offsetY));

        return INSTANCE;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            DeathTimerMod.LOGGER.error("Error saving config", e);
        }
    }
}