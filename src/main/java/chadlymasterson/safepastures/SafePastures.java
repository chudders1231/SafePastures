package chadlymasterson.safepastures;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class SafePastures implements ModInitializer {
    public static final String MOD_ID = "safepastures";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ConfigLoader config;

    @Override
    public void onInitialize() {
        registerEvents();
    }

    private void registerEvents() {
        // Subscribe to the server start event
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStartTick);
    }

    private void onServerStartTick(MinecraftServer server) {
        ServerWorld world = server.getOverworld();

        if (Boolean.getBoolean("fabric.development")) {
            LOGGER.info("Running in development mode!");
            // Dev-only code here

            LOGGER.info(getConfig(world).toJsonString());
        }
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static ConfigLoader getConfig(ServerWorld serverWorld) {
        if (config == null) {
            config = new ConfigLoader("safepastures.json", serverWorld);
            config.load();
        }

        return config;
    }

}
