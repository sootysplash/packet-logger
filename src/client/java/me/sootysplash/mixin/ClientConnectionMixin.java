package me.sootysplash.mixin;

import me.sootysplash.MainPL;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Shadow
    @Final
    private NetworkSide side;

    @Inject(method = "handlePacket", at = @At("HEAD"))
    private static <T extends PacketListener> void hookEventPacketReceive(Packet<T> packet, PacketListener listener, CallbackInfo callback) {
        MainPL.addPacket(packet, true);
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"))
    private void send(Packet<?> packet, PacketCallbacks packetCallback, CallbackInfo callback) {
        if (side == NetworkSide.CLIENTBOUND) {
            MainPL.addPacket(packet, false);
        }
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void hookDisconnect(Text disconnectReason, CallbackInfo ci) {
        MainPL.dump();
    }
}
