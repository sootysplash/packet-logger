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

            ConfigCategory packetblacklist = builder.getOrCreateCategory(Text.of("Packet Blacklist"));
            ConfigEntryBuilder packet = builder.entryBuilder();

            packetblacklist.addEntry(packet.startBooleanToggle(Text.of("Skip Ping and Pong packets"), config.pingPong)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.pingPong = newValue)
                    .build());

            packetblacklist.addEntry(packet.startBooleanToggle(Text.of("Skip PlayerMove packets"), config.playerMove)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.playerMove = newValue)
                    .build());

            packetblacklist.addEntry(packet.startBooleanToggle(Text.of("Skip ChunkData packets"), config.chunkData)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.chunkData = newValue)
                    .build());

            packetblacklist.addEntry(packet.startBooleanToggle(Text.of("Skip BlockUpdate packets"), config.blockData)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.blockData = newValue)
                    .build());

            packetblacklist.addEntry(packet.startBooleanToggle(Text.of("Skip HealthUpdate packets"), config.healthUpdate)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.healthUpdate = newValue)
                    .build());

            ConfigCategory packetData = builder.getOrCreateCategory(Text.of("Packet Data"));
            ConfigEntryBuilder data = builder.entryBuilder();

            packetData.addEntry(data.startBooleanToggle(Text.of("Extra Outgoing Packet Data"), config.packetData)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.packetData = newValue)
                    .build());

            return builder.build();
        };
    }

}
