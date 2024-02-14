package me.sootysplash;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
        String str = String.format("[%s] %s %s", getCurrentHourStamp(), incoming ? "INC" : "SEND", name);

        if(config.packetData) {

            if (packet instanceof ClickSlotC2SPacket cs) {
                str = str.concat(String.format(" Slot: %s, Stack: %s, Action: %s, Sync: %s", cs.getSlot(), cs.getStack(), cs.getActionType().name(), cs.getSyncId()));
            }

            if (packet instanceof UpdateSelectedSlotC2SPacket us) {
                str = str.concat(String.format(" Slot: %s", us.getSelectedSlot()));
            }

            if (packet instanceof PlayerInteractEntityC2SPacket pie && MinecraftClient.getInstance().world != null) {
                Entity e = MinecraftClient.getInstance().world.getEntityById(pie.entityId);
                String eName = e == null ? "null" : e.getName().getLiteralString();
                str = str.concat(String.format(" Entity: %s, Sneaking: %b", eName, pie.isPlayerSneaking()));
            }

            if (packet instanceof ClientStatusC2SPacket cs) {
                str = str.concat(String.format(" Action: %s", cs.getMode().name()));
            }

            if (packet instanceof ChatMessageC2SPacket cm) {
                str = str.concat(String.format(" Message: %s", cm.chatMessage()));
            }

            if (packet instanceof CraftRequestC2SPacket cr) {
                str = str.concat(String.format(" Recipe: %s, Sync: %s", cr.getRecipe(), cr.getSyncId()));
            }

            if (packet instanceof PlayerInteractItemC2SPacket pii) {
                str = str.concat(String.format(" Hand: %s", pii.getHand().name()));
            }

            if (packet instanceof PickFromInventoryC2SPacket pfi) {
                str = str.concat(String.format(" Slot: %s", pfi.getSlot()));
            }

            if (packet instanceof SlotChangedStateC2SPacket scs) {
                str = str.concat(String.format(" Slot: %s, NewState: %b, Sync: %s", scs.slotId(), scs.newState(), scs.screenHandlerId()));
            }

            if (packet instanceof CustomPayloadC2SPacket cp) {
                str = str.concat(String.format(" Payload: %s", cp.payload().id()));
            }

            if (packet instanceof CommandExecutionC2SPacket ce) {
                str = str.concat(String.format(" Command: %s", ce.command()));
            }

            if (packet instanceof PlayerInteractBlockC2SPacket pib) {
                BlockHitResult bhr = pib.getBlockHitResult();
                str = str.concat(String.format(" Hand: %s, Pos: %s, Side: %s, BlockPos: %s", pib.getHand(), bhr.getPos(), bhr.getSide(), bhr.getBlockPos()));
            }

            if (packet instanceof ClientOptionsC2SPacket co) {
                str = str.concat(String.format(" Options: %s", co.options().toString()));
            }

            if (packet instanceof HandSwingC2SPacket hs) {
                str = str.concat(String.format(" Hand: %s", hs.getHand()));
            }

            if (packet instanceof PlayerInputC2SPacket pi) {
                str = str.concat(String.format(" Forward: %s, Sideways: %s, Jumping: %s, Sneaking: %s", pi.getForward(), pi.getSideways(), pi.isJumping(), pi.isSneaking()));
            }

            if (packet instanceof PlayerActionC2SPacket pa) {
                str = str.concat(String.format(" Action: %s, Direction: %s, Position: %s", pa.getAction().name(), pa.getDirection().getName(), pa.getPos()));
            }

            ClientPlayerEntity p = MinecraftClient.getInstance().player;
            if (packet instanceof PlayerMoveC2SPacket pm && p != null) {
                str = str.concat(String.format(" Pitch: %s, Yaw: %s, X: %s, Y: %s, Z: %s, Ground: %s", pm.getPitch(p.getPitch()), pm.getYaw(p.getYaw()), pm.getX(p.getX()), pm.getY(p.getY()), pm.getZ(p.getZ()), pm.isOnGround()));
            }

            if (packet instanceof VehicleMoveC2SPacket vm && p != null) {
                str = str.concat(String.format(" Pitch: %s, Yaw: %s, X: %s, Y: %s, Z: %s", vm.getPitch(), vm.getYaw(), vm.getX(), vm.getY(), vm.getZ()));
            }

            if (packet instanceof UpdatePlayerAbilitiesC2SPacket upa) {
                str = str.concat(String.format(" Flying: %s", upa.isFlying()));
            }

            if (packet instanceof CloseHandledScreenC2SPacket chs) {
                str = str.concat(String.format(" Sync: %s", chs.getSyncId()));
            }

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

        File newDirectory = new File(FabricLoader.getInstance().getGameDir().toString(), "packet-logs");
        if(newDirectory.mkdir() || newDirectory.exists()) {
            Path file = FabricLoader.getInstance().getGameDir().resolve("packet-logs").resolve(String.format("packets-%s.json", getCurrentTimeStamp()));
            try {
                Files.write(file, packets);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            synchronized (packets) {
                packets.clear();
            }
        }

    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }
    public static String getCurrentHourStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ConfigPL.class, GsonConfigSerializer::new);
        LOGGER.info("PacketLogger | Sootysplash was here");
    }
}