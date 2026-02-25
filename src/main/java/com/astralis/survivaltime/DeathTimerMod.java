package com.astralis.survivaltime;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathTimerMod implements ModInitializer {

    // Constants
    public static final String MOD_ID = "survivaltime";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** World tick at which the player last died (-1 = timer not yet started). */
    private static long lastDeathTick = -1;

    // Initialization
    @Override
    public void onInitialize() {
        LOGGER.info("Survival Time initialized");
        DeathTimerConfig.getInstance();
    }

    // Timer API (used by the client renderer)
    public static long getLastDeathTick() {
        return lastDeathTick;
    }

    public static void setLastDeathTick(long tick) {
        lastDeathTick = tick;
    }

    /** Clears the stored death tick, effectively resetting the timer. */
    public static void resetDeathTimer() {
        lastDeathTick = -1;
    }
}
