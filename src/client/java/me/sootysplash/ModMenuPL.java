package me.sootysplash;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;


public class ModMenuPL implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigPL config = ConfigPL.getInstance();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Config"))
                    .setSavingRunnable(config::save);

            ConfigCategory handle = builder.getOrCreateCategory(Text.of("Handling"));
            ConfigEntryBuilder cfghandle = builder.entryBuilder();

            handle.addEntry(cfghandle.startBooleanToggle(Text.of("Log incoming packets"), config.incoming)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.incoming = newValue)
                    .build());

            handle.addEntry(cfghandle.startBooleanToggle(Text.of("Log outgoing packets"), config.outgoing)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.outgoing = newValue)
                    .build());

            return builder.build();
        };
    }

}
