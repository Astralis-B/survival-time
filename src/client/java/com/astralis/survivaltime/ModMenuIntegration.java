package com.astralis.survivaltime;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModMenuIntegration implements ModMenuApi {

    // ModMenu entry point
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            DeathTimerConfig config = DeathTimerConfig.getInstance();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("Survival Time"))
                    .setSavingRunnable(config::save)
                    .transparentBackground()
                    .setDoesConfirmSave(false);

            ConfigCategory general = builder.getOrCreateCategory(Text.literal("Settings"));
            ConfigEntryBuilder entry = builder.entryBuilder();

            // General
            general.addEntry(entry.startTextDescription(Text.literal(Formatting.WHITE + "General")).build());

            general.addEntry(entry.startBooleanToggle(Text.literal("Enabled"), config.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Enable or disable the timer display"))
                    .setSaveConsumer(v -> config.enabled = v)
                    .build());

            general.addEntry(entry.startBooleanToggle(Text.literal("Death Animation"), config.deathAnimationEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Show time survived when dying"))
                    .setSaveConsumer(v -> config.deathAnimationEnabled = v)
                    .build());

            general.addEntry(entry.startEnumSelector(Text.literal("Time Format"), DeathTimerConfig.TimeMode.class, config.timeMode)
                    .setDefaultValue(DeathTimerConfig.TimeMode.MINECRAFT_DAYS)
                    .setEnumNameProvider(v -> Text.literal(formatEnumName(v.name())))
                    .setTooltip(Text.literal("Real Time: Decimal format (e.g., 1.50 Hours)\nMinecraft Days: Decimal format (e.g., 2.75 Days)"))
                    .setSaveConsumer(v -> config.timeMode = v)
                    .build());

            // Position
            general.addEntry(entry.startTextDescription(Text.literal(Formatting.WHITE + "Position Settings"))
                    .setTooltip(Text.literal("Default positions:\n• Corners: 4px from edges\n• Center Top: 4px from top\n• Action Bar: 5px above item text"))
                    .build());

            general.addEntry(entry.startEnumSelector(Text.literal("Position"), DeathTimerConfig.Position.class, config.position)
                    .setDefaultValue(DeathTimerConfig.Position.CENTER_TOP)
                    .setEnumNameProvider(v -> Text.literal(formatEnumName(v.name())))
                    .setTooltip(Text.literal("Base position for the timer display"))
                    .setSaveConsumer(v -> config.position = v)
                    .build());

            general.addEntry(entry.startIntField(Text.literal("Horizontal Offset"), config.offsetX)
                    .setDefaultValue(0).setMin(-1000).setMax(1000)
                    .setTooltip(Text.literal("Adjust horizontal position\n• Positive = right\n• Negative = left"))
                    .setSaveConsumer(v -> config.offsetX = v)
                    .build());

            general.addEntry(entry.startIntField(Text.literal("Vertical Offset"), config.offsetY)
                    .setDefaultValue(0).setMin(-1000).setMax(1000)
                    .setTooltip(Text.literal("Adjust vertical position\n• Positive = down\n• Negative = up"))
                    .setSaveConsumer(v -> config.offsetY = v)
                    .build());

            return builder.build();
        };
    }

    /** Converts SCREAMING_SNAKE_CASE enum names to Title Case for display. */
    private String formatEnumName(String name) {
        String[] words = name.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        return result.toString().trim();
    }
}
