package deliciousbread481.placewatchdebug;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

/**
 * Sets up two dedicated Log4j2 file loggers:
 *   logs/placewatchdebug/client.log  (physical client only)
 *   logs/placewatchdebug/server.log  (dedicated server OR integrated logical server)
 * Each logger is non-additive so it does NOT spam the main game log.
 */
public final class PWLog {
    private static final String PATTERN =
        "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] [%-6level] %msg%n";

    private static Logger CLIENT;
    private static Logger SERVER;
    private static Logger COMMON;

    private PWLog() {}

    public static synchronized void init() {
        if (COMMON != null) return;
        boolean client = FMLLaunchHandler.side().isClient();

        // On the client jar both sides can exist (integrated server), so build both files.
        CLIENT = client ? build("PlaceWatchDebugClient", "logs/placewatchdebug/client.log") : null;
        SERVER = build("PlaceWatchDebugServer", "logs/placewatchdebug/server.log");
        COMMON = SERVER; // common messages funnel to server.log; overridden per-call where needed
    }

    private static Logger build(String name, String path) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
                .withPattern(PATTERN)
                .build();

        FileAppender appender = FileAppender.newBuilder()
                .setName(name + "Appender")
                .withFileName(path)
                .withAppend(true)
                .withLocking(false)
                .setLayout(layout)
                .setConfiguration(config)
                .withImmediateFlush(true)
                .build();
        appender.start();
        config.addAppender(appender);

        AppenderRef ref = AppenderRef.createAppenderRef(appender.getName(), Level.ALL, null);
        LoggerConfig lc = LoggerConfig.createLogger(false, Level.ALL, name,
                "false", new AppenderRef[]{ ref }, null, config, null);
        lc.addAppender(appender, Level.ALL, null);
        config.addLogger(name, lc);
        ctx.updateLoggers();

        return LogManager.getContext(false).getLogger(name);
    }

    /** Writes to server.log (also used for common/network messages on the server). */
    public static Logger server() { return SERVER != null ? SERVER : LogManager.getLogger("PlaceWatchDebugServer"); }

    /** Writes to client.log; falls back to server logger on a dedicated server. */
    public static Logger client() { return CLIENT != null ? CLIENT : server(); }

    /** Convenience: pick the right file based on the calling side. */
    public static Logger common() {
        return FMLLaunchHandler.side().isClient() ? client() : server();
    }
}