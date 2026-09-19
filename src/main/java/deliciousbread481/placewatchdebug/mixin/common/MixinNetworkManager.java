package deliciousbread481.placewatchdebug.mixin.common;

import deliciousbread481.placewatchdebug.PWLog;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.ITextComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "exceptionCaught", at = @At("HEAD"), remap = true)
    private void placewatch$exceptionCaught(ChannelHandlerContext ctx, Throwable cause, CallbackInfo ci) {
        PWLog.common().warn("[NET exceptionCaught] channel={} cause={}",
                ctx == null ? "?" : ctx.channel(), String.valueOf(cause), cause);
    }

    @Inject(method = "channelInactive", at = @At("HEAD"), remap = true)
    private void placewatch$channelInactive(ChannelHandlerContext ctx, CallbackInfo ci) {
        PWLog.common().warn("[NET channelInactive] channel={} (connection closed/dropped)",
                ctx == null ? "?" : ctx.channel());
    }

    // Logs the human-readable disconnect reason (kick/timeout/etc.)
    @Inject(method = "closeChannel", at = @At("HEAD"), remap = true)
    private void placewatch$closeChannel(ITextComponent message, CallbackInfo ci) {
        PWLog.common().warn("[NET closeChannel] reason={}", message == null ? "?" : message.getUnformattedText());
    }
}