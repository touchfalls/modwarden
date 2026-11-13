package io.github.touchfalls.modwarden.server;

import io.github.touchfalls.modwarden.config.ModsConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModWardenTest {
    @Test
    void whitelistAllowsSystemAndFabricModulesOnly() {
        assertTrue(ModsConfig.isModAllowed("minecraft"));
        assertTrue(ModsConfig.isModAllowed("modwarden"));
        assertTrue(ModsConfig.isModAllowed("mixinextras"));
        assertTrue(ModsConfig.isModAllowed("fabric-data-attachment-api-v1"));
        assertFalse(ModsConfig.isModAllowed("sodium"));
    }

    @Test
    void messagesFollowClientLanguage() {
        assertTrue(ModListServer.message("fr_fr", "error.remove_mods").contains("Supprimez"));
        assertTrue(ModListServer.message("ES_MX", "error.remove_mods").contains("Elimina"));
        assertTrue(ModListServer.message("pt_br", "error.remove_mods").contains("Remove"));
        assertTrue(ModListServer.message(null, "error.remove_mods").contains("Remove"));
        ModListServer.MESSAGES.forEach((lang, msgs) ->
            assertEquals(ModListServer.MESSAGES.get("en").keySet(), msgs.keySet(), lang + " messages differ"));
    }
}
