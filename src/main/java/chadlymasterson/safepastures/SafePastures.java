package chadlymasterson.safepastures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod("safepastures_neoforge")
public final class SafePastures {

    public static final String MOD_ID = "safepastures_neoforge";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ConfigLoader config;

    public SafePastures() {
        NeoForge.EVENT_BUS.register(SafePastures.class);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel world = event.getServer().overworld();

        if(Boolean.getBoolean("neoforge.development")) {
            LOGGER.info("Running in development mode!");
            // Dev-only code here

            LOGGER.info(getConfig(world).toJsonString());
        }

    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.tryParse(String.format("%s:%s", MOD_ID, path));
    }

    public static ConfigLoader getConfig(ServerLevel serverWorld) {
        if (config == null) {
            config = new ConfigLoader("safepastures.json", serverWorld);
            config.load();
        }

        return config;
    }

}
