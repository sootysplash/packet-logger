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

            ConfigCategory packetblacklist = builder.getOrCreateCategory(Text.of("Packet Blacklist"));
            ConfigEntryBuilder packet = builder.entryBuilder();

            packetblacklist.addEntry(packet.startBooleanToggle(Text.of("Skip Ping packets"), config.pingPong)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.pingPong = newValue)
                    .build());

            packetblacklist.addEntry(packet.startBooleanToggle(Text.of("Skip PlayerMove packets"), config.playerMove)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.playerMove = newValue)
                    .build());

            return builder.build();
        };
    }

}
