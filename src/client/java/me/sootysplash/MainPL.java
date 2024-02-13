package me.sootysplash;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainPL implements ClientModInitializer {
    private static final List<String> packets = new ArrayList<>();
    public static Logger LOGGER = LoggerFactory.getLogger("PacketLogger");

    public static void addPacket(Packet<?> packet, boolean incoming) {
        String name = packet.getClass().getName();
        name = name.substring(name.lastIndexOf('.') + 1);
        String str = String.format("[%s] %s %s", getCurrentTimeStamp(), incoming ? "INC" : "SEND", name);
        ConfigPL config = ConfigPL.getInstance();
        if (!config.outgoing && !incoming)
            return;

        if (!config.incoming && incoming)
            return;

        synchronized (packets) {
            packets.add(str);
        }
    }

    public static void dump() {
        // hacky solution
        if (packets.size() < 12)
            return;

        Path file = FabricLoader.getInstance().getGameDir().resolve(String.format("packets-%s.json", getCurrentTimeStamp()));
        try {
            Files.write(file, packets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        synchronized (packets) {
            packets.clear();
        }

    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ConfigPL.class, GsonConfigSerializer::new);
        LOGGER.info("PacketLogger | Sootysplash was here");
    }
}