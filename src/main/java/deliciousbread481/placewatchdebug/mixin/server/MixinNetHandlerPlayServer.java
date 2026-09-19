package deliciousbread481.placewatchdebug.mixin.server;

import deliciousbread481.placewatchdebug.PWCorrelation;
import deliciousbread481.placewatchdebug.PWLog;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {

    @Shadow public EntityPlayerMP player;

    @Inject(method = "processTryUseItemOnBlock", at = @At("HEAD"), remap = true)
    private void placewatch$recvTryUse(CPacketPlayerTryUseItemOnBlock packet, CallbackInfo ci) {
        String who = player == null ? "?" : player.getName();
        String cid = PWCorrelation.key(who, packet.getPos());
        PWLog.server().info(
            "[RECV CPacketTryUseItemOnBlock] cid={} pos={} face={} hand={} held={}",
            cid, packet.getPos(), packet.getDirection(), packet.getHand(),
            player == null ? "?" : player.getHeldItem(packet.getHand()));
    }
}