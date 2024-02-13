package me.sootysplash;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.text.Text;


public class ModMenuOE implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigOE config = ConfigOE.getInstance();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Config"))
                    .setSavingRunnable(config::save);

            ConfigCategory handle = builder.getOrCreateCategory(Text.of("Handling"));
            ConfigEntryBuilder cfghandle =  builder.entryBuilder();

            handle.addEntry(cfghandle.startBooleanToggle(Text.of("Enabled"), config.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Optimally eat?\nNOTE: With this mod enabled you cannot hold rmb to continually eat food!"))
                    .setSaveConsumer(newValue -> config.enabled = newValue)
                    .build());

            return builder.build();
        };
    }

}
