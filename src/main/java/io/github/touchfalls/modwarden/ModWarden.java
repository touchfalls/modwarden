package io.github.touchfalls.modwarden;

import io.github.touchfalls.modwarden.config.ModsConfig;
import io.github.touchfalls.modwarden.network.ModListPacket;
import io.github.touchfalls.modwarden.server.ModListServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModWarden implements ModInitializer {
    public static final String MOD_ID = "modwarden";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ModWarden initialized!");

        ModListPacket.registerServerReceiver();
        ModListServer.initialize();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ModsConfig.loadConfig();
            LOGGER.info("ModWarden configuration loaded");
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            server.execute(() -> ModListServer.scheduleTimeoutCheck(handler.player, server)));
    }
}
