package deliciousbread481.placewatchdebug.mixin.client;

import deliciousbread481.placewatchdebug.PWLog;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Inject(method = "handleBlockChange", at = @At("HEAD"), remap = true)
    private void placewatch$blockChange(SPacketBlockChange packet, CallbackInfo ci) {
        PWLog.client().info("[RECV SPacketBlockChange] pos={} serverState={} <-- possible ROLLBACK/correction",
                packet.getBlockPosition(), packet.getBlockState());
    }

    @Inject(method = "handleMultiBlockChange", at = @At("HEAD"), remap = true)
    private void placewatch$multiBlockChange(SPacketMultiBlockChange packet, CallbackInfo ci) {
        int n = packet.getChangedBlocks() == null ? -1 : packet.getChangedBlocks().length;
        PWLog.client().info("[RECV SPacketMultiBlockChange] count={} <-- possible batched ROLLBACK", n);
    }
}