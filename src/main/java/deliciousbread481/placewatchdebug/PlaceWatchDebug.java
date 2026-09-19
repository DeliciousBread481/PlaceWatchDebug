package deliciousbread481.placewatchdebug;

import deliciousbread481.placewatchdebug.event.PWServerEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

@Mod(modid = PlaceWatchDebug.MODID, name = PlaceWatchDebug.NAME, version = PlaceWatchDebug.VERSION,
     acceptableRemoteVersions = "*")
public class PlaceWatchDebug {
    public static final String MODID   = "placewatchdebug";
    public static final String NAME    = "PlaceWatchDebug";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        PWConfig.load(e.getSuggestedConfigurationFile());
        PWLog.init();                       // sets up client.log / server.log appenders
        PWLog.common().info("PlaceWatchDebug preInit, side={}", FMLLaunchHandler.side());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        // Server / logical-server side Forge events
        MinecraftForge.EVENT_BUS.register(new PWServerEventHandler());
        // Client-only events registered from the client proxy path
        if (FMLLaunchHandler.side().isClient()) {
            MinecraftForge.EVENT_BUS.register(
                new deliciousbread481.placewatchdebug.event.PWClientEventHandler());
        }
        PWLog.common().info("PlaceWatchDebug init done.");
    }
}