package deliciousbread481.placewatchdebug;

import net.minecraft.util.math.BlockPos;

/**
 * Builds a short deterministic correlation key from (player, pos) so that
 * a client "send" line and a server "receive/result/rollback" line can be matched
 * across client.log and server.log.
 */
public final class PWCorrelation {
    private PWCorrelation() {}

    public static String key(String player, BlockPos pos) {
        return player + "@" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static String key(String player, int x, int y, int z) {
        return player + "@" + x + "," + y + "," + z;
    }
}