package com.astralis.survivaltime;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;

public class DeathTimerRenderer implements ClientModInitializer {

    // Normal timer state
    private long    lastKnownTicks  = -1;  // último valor de TIME_SINCE_DEATH recibido
    private long    lastSyncMs      = -1;  // momento en que se recibió ese valor
    private boolean wasAlive        = true;
    private int     tickCounter     = 0;

    // Death animation state
    private long    deathAnimationStartTime = -1;
    private boolean isDeathAnimationActive  = false;
    private long    deathAnimationTicks     = 0;
    private long    targetSurvivalTicks     = 0;
    private int     animationAlpha          = 255;

    @Override
    @SuppressWarnings("deprecation")
    public void onInitializeClient() {
        DeathTimerMod.LOGGER.info("Survival Time Renderer initialized");
        HudRenderCallback.EVENT.register(this::render);

        // Solicita las estadísticas al servidor cada segundo para mantener TIME_SINCE_DEATH actualizado
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getNetworkHandler() == null) return;
            if (tickCounter++ % 20 == 0) {
                client.getNetworkHandler().sendPacket(
                        new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS)
                );
            }
        });
    }

    // Main render loop (called every frame)
    private void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        PlayerEntity player = client.player;
        DeathTimerConfig config = DeathTimerConfig.getInstance();
        boolean isAlive = player.isAlive();

        // Transition: alive -> dead
        if (wasAlive && !isAlive && config.deathAnimationEnabled) {
            startDeathAnimation();
        }

        if (!isAlive && isDeathAnimationActive) {
            updateDeathAnimation();
        } else if (isAlive) {
            isDeathAnimationActive = false;
        }

        wasAlive = isAlive;

        if (isDeathAnimationActive) {
            renderDeathAnimation(context, client, config);
        } else if (config.enabled && isAlive) {
            renderNormalTimer(context, client, config);
        }
    }

    // Timer management

    private long getTimeSinceDeathTicks(MinecraftClient client) {
        return client.player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
    }

    /**
     * Devuelve los ticks transcurridos desde la última muerte, interpolados suavemente
     * entre actualizaciones del servidor. Nunca retrocede.
     */
    private long getCurrentTicks(MinecraftClient client) {
        long serverTicks = getTimeSinceDeathTicks(client);

        // Primera vez o tras un reset: ancla el valor actual
        if (lastKnownTicks < 0) {
            lastKnownTicks = serverTicks;
            lastSyncMs     = System.currentTimeMillis();
        }

        // Si llegó un valor nuevo y mayor del servidor, anclamos
        // Si el servidor manda un valor menor (lag/interpolación adelantada), lo ignoramos
        if (serverTicks > lastKnownTicks) {
            lastKnownTicks = serverTicks;
            lastSyncMs     = System.currentTimeMillis();
        }

        // Si está pausado, reancla para evitar salto al volver
        if (client.isPaused()) {
            lastSyncMs = System.currentTimeMillis();
            return lastKnownTicks;
        }

        // Interpola suavemente desde el último valor conocido
        long msSinceSync = lastSyncMs < 0 ? 0 : System.currentTimeMillis() - lastSyncMs;
        return lastKnownTicks + (msSinceSync * 20 / 1000);
    }

    private void startDeathAnimation() {
        targetSurvivalTicks     = Math.max(0, lastKnownTicks);
        deathAnimationTicks     = targetSurvivalTicks;
        deathAnimationStartTime = System.currentTimeMillis();
        isDeathAnimationActive  = true;
        animationAlpha          = 255;
        lastKnownTicks          = -1;
        lastSyncMs              = -1;
    }

    // Death animation logic

    /**
     * Animation timeline (8 s total):
     *   0-4 s  -> cubic ease-out count-down from survived time to 0
     *   4-6 s  -> hold at 0 (full opacity)
     *   6-8 s  -> hold at 0, fade out to transparent
     */
    private void updateDeathAnimation() {
        long elapsed = System.currentTimeMillis() - deathAnimationStartTime;

        if (elapsed >= 8000) {
            isDeathAnimationActive = false;
            return;
        }

        if (elapsed < 4000) {
            float progress = (float) Math.pow(elapsed / 4000f, 3.0);
            deathAnimationTicks = (long) Math.max(0, targetSurvivalTicks * (1.0f - progress));
        } else {
            deathAnimationTicks = 0;
            if (elapsed > 6000) {
                animationAlpha = (int) MathHelper.lerp((elapsed - 6000) / 2000f, 255, 0);
            }
        }
    }

    // Rendering

    private void renderDeathAnimation(DrawContext context, MinecraftClient client, DeathTimerConfig config) {
        if (animationAlpha <= 0) return;
        String text = formatTime(deathAnimationTicks, config.timeMode) + " survived";
        int color   = animationAlpha < 255 ? (animationAlpha << 24) | 0xFFFFFF : 0xFFFFFFFF;
        renderText(context, client, config, text, color);
    }

    private void renderNormalTimer(DrawContext context, MinecraftClient client, DeathTimerConfig config) {
        long ticks = getCurrentTicks(client);
        renderText(context, client, config, formatTime(ticks, config.timeMode) + " survived", 0xFFFFFFFF);
    }

    private void renderText(DrawContext context, MinecraftClient client, DeathTimerConfig config, String text, int color) {
        TextRenderer textRenderer = client.textRenderer;
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        int tw = textRenderer.getWidth(text);
        int th = textRenderer.fontHeight;

        int x, y;
        switch (config.position) {
            case TOP_LEFT     -> { x = 4;           y = 4; }
            case TOP_RIGHT    -> { x = sw - tw - 4; y = 4; }
            case BOTTOM_LEFT  -> { x = 4;           y = sh - th - 4; }
            case BOTTOM_RIGHT -> { x = sw - tw - 4; y = sh - th - 4; }
            case ACTION_BAR   -> { x = sw / 2 - tw / 2; y = sh - 55 - th; }
            default           -> { x = sw / 2 - tw / 2; y = 4; }  // CENTER_TOP
        }

        x = Math.max(0, Math.min(sw - tw, x + config.offsetX));
        y = Math.max(0, Math.min(sh - th, y + config.offsetY));

        context.drawTextWithShadow(textRenderer, text, x, y, color);
    }

    // Time formatting

    private String formatTime(long ticks, DeathTimerConfig.TimeMode mode) {
        return mode == DeathTimerConfig.TimeMode.REAL_TIME
                ? formatRealTime(ticks)
                : formatMinecraftTime(ticks);
    }

    private String formatRealTime(long ticks) {
        double s = ticks / 20.0;
        if (s < 60)          return String.format("%.2f Seconds", s);
        if (s < 3_600)       return String.format("%.2f Minutes", s / 60);
        if (s < 86_400)      return String.format("%.2f Hours",   s / 3_600);
        if (s < 2_592_000)   return String.format("%.2f Days",    s / 86_400);
        if (s < 31_536_000)  return String.format("%.2f Months",  s / 2_592_000);
        return                      String.format("%.2f Years",   s / 31_536_000);
    }

    private String formatMinecraftTime(long ticks) {
        double d = ticks / 24_000.0;
        if (d < 1)   return String.format("%.2f Hours",  d * 24);
        if (d < 30)  return String.format("%.2f Days",   d);
        if (d < 360) return String.format("%.2f Months", d / 30);
        return              String.format("%.2f Years",  d / 360);
    }
}