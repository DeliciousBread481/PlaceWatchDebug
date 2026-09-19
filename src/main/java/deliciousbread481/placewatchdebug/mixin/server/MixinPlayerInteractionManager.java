package deliciousbread481.placewatchdebug.mixin.server;

import deliciousbread481.placewatchdebug.PWConfig;
import deliciousbread481.placewatchdebug.PWCorrelation;
import deliciousbread481.placewatchdebug.PWLog;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInteractionManager.class)
public class MixinPlayerInteractionManager {

    // Logs the final authoritative result of the server-side placement pipeline.
    @Inject(method = "processRightClickBlock", at = @At("RETURN"), remap = true)
    private void placewatch$result(EntityPlayer player, World world, ItemStack stack, EnumHand hand,
                                   BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                   CallbackInfoReturnable<EnumActionResult> cir) {
        String cid = PWCorrelation.key(player.getName(), pos);
        EnumActionResult r = cir.getReturnValue();
        PWLog.server().info(
            "[RESULT processRightClickBlock] cid={} result={} serverStateNow={} heldAfter={}",
            cid, r, world.getBlockState(pos), stack);

        // If the pipeline did not SUCCEED, the server will typically resync (SPacketBlockChange),
        // which the CLIENT sees as the ghost block disappearing.
        if (r != EnumActionResult.SUCCESS) {
            PWLog.server().warn(
                "[POSSIBLE ROLLBACK] cid={} result={} != SUCCESS -> server may resync client (ghost block). "
                + "Cross-check client.log for a matching [RECV SPacketBlockChange] pos={}.",
                cid, r, pos);
            if (PWConfig.stackTraceOnCancel) {
                StringBuilder sb = new StringBuilder("[POSSIBLE ROLLBACK stack] cid=").append(cid).append('\n');
                for (StackTraceElement el : Thread.currentThread().getStackTrace())
                    sb.append("    at ").append(el).append('\n');
                PWLog.server().warn(sb.toString());
            }
        }
    }
}