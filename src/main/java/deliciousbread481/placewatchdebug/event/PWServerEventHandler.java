package deliciousbread481.placewatchdebug.event;

import deliciousbread481.placewatchdebug.PWConfig;
import deliciousbread481.placewatchdebug.PWCorrelation;
import deliciousbread481.placewatchdebug.PWLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PWServerEventHandler {

    // ---- Right-click interaction: shows WHO cancels/DENYs the placement ----
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        if (e.getWorld().isRemote) return; // server-side authority
        String cid = PWCorrelation.key(name(e.getEntityPlayer()), e.getPos());
        boolean anomaly = e.isCanceled()
                || e.getUseBlock() == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY
                || e.getUseItem()  == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY;
        if (PWConfig.logAll || anomaly) {
            PWLog.server().info(
                "[EVT RightClickBlock] cid={} hand={} face={} useBlock={} useItem={} canceled={} cancelResult={}",
                cid, e.getHand(), e.getFace(), e.getUseBlock(), e.getUseItem(),
                e.isCanceled(), e.getCancellationResult());
        }
        if (anomaly && PWConfig.stackTraceOnCancel) dumpStack("RightClickBlock cancelled/DENY cid=" + cid);
    }

    // ---- Single block placed ----
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onPlace(BlockEvent.EntityPlaceEvent e) {
        if (e.getWorld().isRemote) return;
        final World world = (World) e.getWorld();
        final BlockPos pos = e.getPos();
        final IBlockState placed = e.getPlacedBlock();
        final String cid = PWCorrelation.key(entityName(e), pos);

        PWLog.server().info("[EVT EntityPlace] cid={} placed={} against={} canceled={}",
                cid, placed, e.getPlacedAgainst(), e.isCanceled());
        if (e.isCanceled() && PWConfig.stackTraceOnCancel)
            dumpStack("EntityPlaceEvent CANCELLED cid=" + cid);

        if (!e.isCanceled() && PWConfig.verifyNextTick) verifyNextTick(world, pos, placed, cid);
    }

    // ---- Multi block placed (bed/door/slab-merge etc). Matches "second block also vanishes". ----
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onMultiPlace(BlockEvent.MultiPlaceEvent e) {
        if (e.getWorld().isRemote) return;
        final String cid = PWCorrelation.key(entityName(e), e.getPos());
        PWLog.server().info("[EVT MultiPlace] cid={} count={} placed={} canceled={}",
                cid, e.getReplacedBlockSnapshots().size(), e.getPlacedBlock(), e.isCanceled());
        if (e.isCanceled() && PWConfig.stackTraceOnCancel)
            dumpStack("MultiPlaceEvent CANCELLED cid=" + cid);
    }

    @SubscribeEvent
    public void onDestroyItem(PlayerDestroyItemEvent e) {
        if (e.getEntity().world.isRemote) return;
        PWLog.server().info("[EVT DestroyItem] player={} stack={} hand={}",
                name(e.getEntityPlayer()), e.getOriginal(), e.getHand());
    }

    // Schedule a 1-tick-later check to catch blocks silently removed after placement.
    private void verifyNextTick(World world, BlockPos pos, IBlockState placed, String cid) {
        net.minecraft.server.MinecraftServer srv = world.getMinecraftServer();
        if (srv == null) return;
        srv.addScheduledTask(() -> {
            IBlockState now = world.getBlockState(pos);
            if (now.getBlock() != placed.getBlock()) {
                PWLog.server().warn(
                    "[VERIFY MISMATCH] cid={} expected={} but now={} -> block was removed/overwritten after placement!",
                    cid, placed, now);
                dumpStack("[VERIFY] removal detected cid=" + cid);
            } else if (PWConfig.logAll) {
                PWLog.server().info("[VERIFY OK] cid={} block still present={}", cid, now);
            }
        });
    }

    private static void dumpStack(String header) {
        StringBuilder sb = new StringBuilder(header).append('\n');
        for (StackTraceElement el : Thread.currentThread().getStackTrace())
            sb.append("    at ").append(el).append('\n');
        PWLog.server().warn(sb.toString());
    }

    private static String name(EntityPlayer p) { return p == null ? "?" : p.getName(); }
    private static String entityName(BlockEvent.EntityPlaceEvent e) {
        return e.getEntity() == null ? "?" : e.getEntity().getName();
    }
    private static String entityName(BlockEvent.MultiPlaceEvent e) {
        return e.getEntity() == null ? "?" : e.getEntity().getName();
    }
}