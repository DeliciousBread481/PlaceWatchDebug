package deliciousbread481.placewatchdebug.event;

import deliciousbread481.placewatchdebug.PWConfig;
import deliciousbread481.placewatchdebug.PWLog;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PWClientEventHandler {
    @SubscribeEvent
    public void onRightClickClient(PlayerInteractEvent.RightClickBlock e) {
        if (!e.getWorld().isRemote) return;
        if (PWConfig.logAll) {
            PWLog.client().info("[EVT(client) RightClickBlock] pos={} hand={} face={} heldMain={}",
                e.getPos(), e.getHand(), e.getFace(),
                e.getEntityPlayer().getHeldItemMainhand());
        }
    }
}