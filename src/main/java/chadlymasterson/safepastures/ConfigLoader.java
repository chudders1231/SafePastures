package chadlymasterson.safepastures;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.include.com.google.common.base.Function;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {

    private static final String CLASS_NAME = ConfigLoader.class.getSimpleName();
    // Path to the config file
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();

    private static File CONFIG_FILE;

    // Config values
    public boolean preventPokemonDamageInPasture = true; // Default to true
    public boolean useBlackList = false; // Defaults to false

    public Map<String, Boolean> damageSourceBlackList = new HashMap<>();

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .setLenient()
            .create();

    public ConfigLoader(String filename, ServerWorld serverWorld) {
        this.CONFIG_FILE = new File(String.valueOf(CONFIG_PATH) + "/safepastures", filename);

        populateDefaultDamageSources(serverWorld);
        load();
    }

    /**
     * Loads this config instance from disk, or creates and saves defaults if it doesn't exist.
     */

    public void load() {
        if (!CONFIG_FILE.exists()) {
            save(); // Create the default values
            return;
        }

        try(FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigLoader loaded = GSON.fromJson(reader, ConfigLoader.class);

            this.preventPokemonDamageInPasture = loaded.preventPokemonDamageInPasture;
            this.useBlackList = loaded.useBlackList;
            this.damageSourceBlackList = loaded.damageSourceBlackList;

        } catch (IOException e) {
            SafePastures.LOGGER.error("Failed to load config: " + e.getMessage());
        }
    }

    /**
     * Saves the current config values to disk.
     */

    public void save() {
        CONFIG_FILE.getParentFile().mkdirs();
        try( FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            SafePastures.LOGGER.error("Failed to save config: " + e.getMessage());
        }
    }

    public void populateDefaultDamageSources(ServerWorld serverWorld) {
        Registry<DamageType> damageTypeRegistry = serverWorld.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE);

        for (Identifier id : damageTypeRegistry.getIds()) {
            damageSourceBlackList.putIfAbsent(id.getPath(), false);
        }
    }

    public String toJsonString() {
        return GSON.toJson(this);
    }
}
