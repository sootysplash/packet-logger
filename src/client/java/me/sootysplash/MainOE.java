package me.sootysplash;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainOE implements ClientModInitializer {
    public static boolean updatedInventory = false;
    public static final Logger LOGGER = LoggerFactory.getLogger("OptimalEat");
    @Override
    public void onInitializeClient() {
        AutoConfig.register(ConfigOE.class, GsonConfigSerializer::new);
        LOGGER.info("OptimalEat | Sootysplash was here");
    }
}