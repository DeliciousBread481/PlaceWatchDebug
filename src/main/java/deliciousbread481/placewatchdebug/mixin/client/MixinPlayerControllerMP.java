package deliciousbread481.placewatchdebug.mixin.client;

import deliciousbread481.placewatchdebug.PWConfig;
import deliciousbread481.placewatchdebug.PWCorrelation;
import deliciousbread481.placewatchdebug.PWLog;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = "processRightClickBlock", at = @At("HEAD"), remap = true)
    private void placewatch$logTryUse(EntityPlayerSP player, WorldClient worldIn, BlockPos pos,
                                      EnumFacing direction, Vec3d vec, EnumHand hand,
                                      CallbackInfoReturnable<EnumActionResult> cir) {
        if (!PWConfig.logAll) return;
        String cid = PWCorrelation.key(player.getName(), pos);
        PWLog.client().info(
            "[SEND CPacketTryUseItemOnBlock] cid={} pos={} face={} hand={} held={} clientStateHere={}",
            cid, pos, direction, hand, player.getHeldItem(hand),
            worldIn.getBlockState(pos));
    }
}