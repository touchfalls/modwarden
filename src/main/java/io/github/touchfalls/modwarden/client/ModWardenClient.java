package io.github.touchfalls.modwarden.client;

import io.github.touchfalls.modwarden.network.ModListPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ModWardenClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("ModWardenClient");

    @Override
    public void onInitializeClient() {
        LOGGER.info("ModWarden Client initialized!");
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            List<String> mods = FabricLoader.getInstance().getAllMods().stream()
                .map(mod -> mod.getMetadata().getId())
                .toList();
            LOGGER.info("Sending mod list to server ({} mods)", mods.size());
            sender.sendPacket(new ModListPacket(mods));
        });
    }
}
