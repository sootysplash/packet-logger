package me.sootysplash;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
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
        ConfigPL config = ConfigPL.getInstance();

        String name = packet.getClass().getName();
        name = name.substring(name.lastIndexOf('.') + 1);
        String str = String.format("[%s] %s %s", getCurrentTimeStamp(), incoming ? "INC" : "SEND", name);

        if(packet instanceof ClickSlotC2SPacket cs && config.clickSlot){
            str = str.concat(String.format(" Slot: %s, Stack: %s, Action: %s", cs.getSlot(), cs.getStack(), cs.getActionType().name()));
        }

        if(packet instanceof UpdateSelectedSlotC2SPacket us && config.selectSlot){
            str = str.concat(String.format(" Slot: %s", us.getSelectedSlot()));
        }

        if(packet instanceof PlayerInteractEntityC2SPacket pie && config.interactEntity && MinecraftClient.getInstance().world != null){
            Entity e = MinecraftClient.getInstance().world.getEntityById(pie.entityId);
            String eName = e == null ? "null" : e.getUuidAsString();
            str = str.concat(String.format(" Entity: %s, Sneaking: %b",  eName, pie.isPlayerSneaking()));
        }


        if (!config.outgoing && !incoming)
            return;

        if (!config.incoming && incoming)
            return;

        if(config.pingPong && (packet instanceof CommonPingS2CPacket || packet instanceof CommonPongC2SPacket))
            return;

        if(config.playerMove && packet instanceof PlayerMoveC2SPacket)
            return;

        if(config.chunkData && (packet instanceof ChunkDataS2CPacket || packet instanceof UnloadChunkS2CPacket))
            return;

        if(config.blockData && packet instanceof BlockUpdateS2CPacket)
            return;

        if(config.healthUpdate && packet instanceof HealthUpdateS2CPacket)
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