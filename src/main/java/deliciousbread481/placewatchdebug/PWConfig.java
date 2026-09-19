package deliciousbread481.placewatchdebug;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public final class PWConfig {
    /** Log every placement attempt (verbose). If false, only anomalies (rollback/cancel) are logged. */
    public static boolean logAll = true;
    /** Print a stack trace when a placement/interaction is cancelled or DENYed. */
    public static boolean stackTraceOnCancel = true;
    /** Verify block still exists 1 tick after placement. */
    public static boolean verifyNextTick = true;

    private PWConfig() {}

    public static void load(File file) {
        Configuration cfg = new Configuration(file);
        cfg.load();
        logAll = cfg.getBoolean("logAll", "general", true,
                "Log every placement attempt. Disable to only capture anomalies.");
        stackTraceOnCancel = cfg.getBoolean("stackTraceOnCancel", "general", true,
                "Dump a stack trace when a placement/interaction is cancelled or DENYed.");
        verifyNextTick = cfg.getBoolean("verifyNextTick", "general", true,
                "Re-check the block state 1 tick after a server placement to detect silent removal.");
        if (cfg.hasChanged()) cfg.save();
    }
}