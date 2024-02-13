package me.sootysplash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.ConfigData;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@me.shedaniel.autoconfig.annotation.Config(name = "OptimalEat")
public class ConfigOE implements ConfigData {

    //Andy is the goat https://github.com/AndyRusso/pvplegacyutils/blob/main/src/main/java/io/github/andyrusso/pvplegacyutils/PvPLegacyUtilsConfig.java

    private static final Path file = FabricLoader.getInstance().getConfigDir().resolve("OptimalEat.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ConfigOE instance;

    public boolean enabled = true;

    public void save() {
        try {
            Files.writeString(file, GSON.toJson(this));
        } catch (IOException e) {
            MainOE.LOGGER.error("OptimalEat could not save the config.");
            throw new RuntimeException(e);
        }
    }

    public static ConfigOE getInstance() {
        if (instance == null) {
            try {
                instance = GSON.fromJson(Files.readString(file), ConfigOE.class);
            } catch (IOException exception) {
                MainOE.LOGGER.warn("OptimalEat couldn't load the config, using defaults.");
                instance = new ConfigOE();
            }
        }

        return instance;
    }

}
