package chadlymasterson.safepastures;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
    // Path to config file
    public static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get()
            .resolve("safepastures");

    private static File CONFIG_FILE;

    // Config Values
    public boolean preventPokemonDamageInPasture = true; // Defaults to true
    public boolean useBlackList = false; // Defaults to false

    public Map<String, Boolean> damageSourceBlackList = new HashMap<>();

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .setLenient()
            .create();

    public ConfigLoader(String filename, ServerLevel serverWorld) {
        this.CONFIG_FILE = new File(String.valueOf(CONFIG_PATH), filename);
        populateDefaultDamageSources(serverWorld);
        load();
    }

    public void load() {
        if(!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigLoader loaded = GSON.fromJson(reader, ConfigLoader.class);

            this.preventPokemonDamageInPasture = loaded.preventPokemonDamageInPasture;
            this.useBlackList = loaded.useBlackList;
            this.damageSourceBlackList = loaded.damageSourceBlackList;
        } catch (IOException e) {
            SafePastures.LOGGER.error("Failed to load config: " + e.getMessage());
        }
    }

    public void save() {
        CONFIG_FILE.getParentFile().mkdirs();
        try( FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            SafePastures.LOGGER.error("Failed to save config: " + e.getMessage());
        }
    }

    public void populateDefaultDamageSources(ServerLevel serverWorld) {
        Registry<DamageType> damageTypeRegistry = serverWorld.registryAccess().registry(Registries.DAMAGE_TYPE)
                .orElseThrow( () -> new RuntimeException("DamageType registry not found."));

        for (ResourceLocation id : damageTypeRegistry.keySet()) {
            damageSourceBlackList.putIfAbsent(id.getPath(), false);
        }
    }

    public String toJsonString() {
        return GSON.toJson(this);
    }
}
