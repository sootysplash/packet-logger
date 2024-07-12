package me.sootysplash;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
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

        try {
            ConfigPL config = ConfigPL.getInstance();
            String str = String.format("[%s] %s ", getCurrentHourStamp(), incoming ? "INC" : "SEND");
            String check = str;

            if (packet instanceof ClickSlotC2SPacket cs) {
                str = str.concat(String.format("ClickSlot Slot: %s, Stack: %s, Action: %s, Sync: %s", cs.getSlot(), cs.getStack(), cs.getActionType().name(), cs.getSyncId()));
            }

            if (packet instanceof UpdateSelectedSlotC2SPacket us) {
                str = str.concat(String.format("UpdateSelectedSlot Slot: %s", us.getSelectedSlot()));
            }

            if (packet instanceof PlayerInteractEntityC2SPacket pie && MinecraftClient.getInstance().world != null) {
                Entity e = MinecraftClient.getInstance().world.getEntityById(pie.entityId);
                String eName = e == null ? "null" : String.valueOf(e.getName());
                str = str.concat(String.format("PlayerInteractEntity Entity: %s, Sneaking: %b", eName, pie.isPlayerSneaking()));
            }

            if (packet instanceof ClientStatusC2SPacket cs) {
                str = str.concat(String.format("ClientStatus Action: %s", cs.getMode().name()));
            }

            if (packet instanceof ChatMessageC2SPacket cm) {
                str = str.concat(String.format("ChatMessage Message: %s", cm.chatMessage()));
            }

            if (packet instanceof CommonPongC2SPacket cp) {
                str = str.concat(String.format("CommonPing Parameter: %s", cp.getParameter()));
                if (config.pingPong)
                    return;
            }

            if (packet instanceof CraftRequestC2SPacket cr) {
                str = str.concat(String.format("CraftRequest Recipe: %s, Sync: %s", cr.getRecipe(), cr.getSyncId()));
            }

            if (packet instanceof PlayerInteractItemC2SPacket pii) {
                str = str.concat(String.format("PlayerInteractItem Hand: %s", pii.getHand().name()));
            }

            if (packet instanceof PickFromInventoryC2SPacket pfi) {
                str = str.concat(String.format("PickFromInventory Slot: %s", pfi.getSlot()));
            }

            if (packet instanceof CommandExecutionC2SPacket ce) {
                str = str.concat(String.format("CommandExecution Command: %s", ce.command()));
            }

            if (packet instanceof HandSwingC2SPacket hs) {
                str = str.concat(String.format("HandSwing Hand: %s", hs.getHand()));
            }

            if (packet instanceof PlayerInputC2SPacket pi) {
                str = str.concat(String.format("PlayerInput Forward: %s, Sideways: %s, Jumping: %s, Sneaking: %s", pi.getForward(), pi.getSideways(), pi.isJumping(), pi.isSneaking()));
            }

            if (packet instanceof PlayerActionC2SPacket pa) {
                str = str.concat(String.format("PlayerAction Action: %s, Direction: %s, Position: %s", pa.getAction().name(), pa.getDirection().getName(), pa.getPos()));
            }
            if (packet instanceof PlayerInteractBlockC2SPacket pib) {
                BlockHitResult bhr = pib.getBlockHitResult();
                str = str.concat(String.format("PlayerInteractBlock Hand: %s, Pos: %s, Side: %s, BlockPos: %s", pib.getHand(), new Vec3d(round(bhr.getPos().x), round(bhr.getPos().y), round(bhr.getPos().z)), bhr.getSide(), bhr.getBlockPos()));
            }

            ClientPlayerEntity p = MinecraftClient.getInstance().player;
            if (packet instanceof PlayerMoveC2SPacket pm && p != null) {
                str = str.concat(String.format("PlayerMove Pitch: %s, Yaw: %s, X: %s, Y: %s, Z: %s, Ground: %s, ChangesLook: %b, ChangesPos: %b", round(pm.getPitch(p.getPitch())), round(pm.getYaw(p.getYaw())), round(pm.getX(p.getX())), round(pm.getY(p.getY())), round(pm.getZ(p.getZ())), pm.isOnGround(), pm.changesLook(), pm.changesPosition()));
                if (config.playerMove)
                    return;
            }

            if (packet instanceof VehicleMoveC2SPacket vm && p != null) {
                str = str.concat(String.format("VehicleMove Pitch: %s, Yaw: %s, X: %s, Y: %s, Z: %s", round(vm.getPitch()), round(vm.getYaw()), round(vm.getX()), round(vm.getY()), round(vm.getZ())));
                if (config.playerMove)
                    return;
            }


            if (packet instanceof UpdatePlayerAbilitiesC2SPacket upa) {
                str = str.concat(String.format("UpdatePlayerAbilities Flying: %s", upa.isFlying()));
            }

            if (packet instanceof CloseHandledScreenC2SPacket chs) {
                str = str.concat(String.format("CloseHandledScreen Sync: %s", chs.getSyncId()));
            }

            if (check.equals(str))
                return;

            synchronized (packets) {
                packets.add(str);
            }
        } catch (Exception e){
            LOGGER.error(e + " " + e.getMessage());
        }
    }

private static double round(double toRound){
    return Math.round(toRound * 100.0) / 100.0;
}

    public static void dump() {
        // hacky solution
        if (packets.size() < 15)
            return;

        File newDirectory = new File(FabricLoader.getInstance().getGameDir().toString(), "packet-logs");
        if (newDirectory.mkdir() || newDirectory.exists()) {
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
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public static String getCurrentHourStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH-mm-ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ConfigPL.class, GsonConfigSerializer::new);
        LOGGER.info("PacketLogger | Sootysplash was here");
    }
}