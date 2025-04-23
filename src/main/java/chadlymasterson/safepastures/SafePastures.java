package chadlymasterson.safepastures;

import com.cobblemon.mod.common.platform.events.ServerEvent;
import com.cobblemon.mod.common.platform.events.ServerTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class SafePastures implements ModInitializer {
    public static final String MOD_ID = "safepastures";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ConfigLoader config;

    @Override
    public void onInitialize() {
        RegisterEvents();
    }

    private void RegisterEvents() {
        // Subscribe to the server start event
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStartTick);
    }

    private void onServerStartTick(MinecraftServer server) {
        ServerWorld world = server.getOverworld();

        if (Boolean.getBoolean("fabric.development")) {
            System.out.println("[SafePastures] Running in development mode!");
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
