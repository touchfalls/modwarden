package io.github.touchfalls.modwarden.server;

import io.github.touchfalls.modwarden.ModWarden;
import io.github.touchfalls.modwarden.config.ModsConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class ModListServer {
    private static final Map<UUID, Integer> pendingChecks = new HashMap<>();
    private static final long TIMEOUT_TICKS = 100L; // 5 seconds at 20 TPS

    // Kick messages are sent by the server, so they cannot rely on the client having our lang files.
    static final Map<String, Map<String, String>> MESSAGES = Map.of(
        "en", Map.of(
            "error.no_mod_list", "§c[ModWarden] Mod list not received!\n§7Make sure you have the ModWarden mod installed",
            "error.no_mod_checker", "§c[ModWarden] ModWarden mod required!\n§7Download the mod to join the server\n§7Mod ID: §fmodwarden",
            "error.unauthorized_mods", "§c[ModWarden] Unauthorized mods detected:\n\n§7",
            "error.whitelist_mode", "Server is running in §eWHITELIST§7 mode.\nOnly mods from the configuration are allowed.\n\n",
            "error.banlist_mode", "Server is running in §eBANLIST§7 mode.\nThese mods are not allowed on the server.\n\n",
            "error.remove_mods", "\n§fRemove these mods to join the server",
            "success.all_mods_checked", "§a[ModWarden] ✓ All mods checked"),
        "ru", Map.of(
            "error.no_mod_list", "§c[ModWarden] Не получена проверка модов!\n§7Убедитесь, что у вас установлен мод ModWarden",
            "error.no_mod_checker", "§c[ModWarden] Требуется мод ModWarden!\n§7Скачайте мод для входа на сервер\n§7ID мода: §fmodwarden",
            "error.unauthorized_mods", "§c[ModWarden] Обнаружены неразрешённые моды:\n\n§7",
            "error.whitelist_mode", "Сервер работает в режиме §eБЕЛОГО СПИСКА§7.\nРазрешены только моды из конфигурации.\n\n",
            "error.banlist_mode", "Сервер работает в режиме §eЧЁРНОГО СПИСКА§7.\nЭти моды запрещены на сервере.\n\n",
            "error.remove_mods", "\n§fУдалите эти моды для входа на сервер",
            "success.all_mods_checked", "§a[ModWarden] ✓ Все моды проверены"),
        "fr", Map.of(
            "error.no_mod_list", "§c[ModWarden] Liste des mods non reçue !\n§7Vérifiez que le mod ModWarden est installé",
            "error.no_mod_checker", "§c[ModWarden] Le mod ModWarden est requis !\n§7Téléchargez le mod pour rejoindre le serveur\n§7ID du mod : §fmodwarden",
            "error.unauthorized_mods", "§c[ModWarden] Mods non autorisés détectés :\n\n§7",
            "error.whitelist_mode", "Le serveur fonctionne en mode §eLISTE BLANCHE§7.\nSeuls les mods de la configuration sont autorisés.\n\n",
            "error.banlist_mode", "Le serveur fonctionne en mode §eLISTE NOIRE§7.\nCes mods ne sont pas autorisés sur le serveur.\n\n",
            "error.remove_mods", "\n§fSupprimez ces mods pour rejoindre le serveur",
            "success.all_mods_checked", "§a[ModWarden] ✓ Tous les mods ont été vérifiés"),
        "it", Map.of(
            "error.no_mod_list", "§c[ModWarden] Lista delle mod non ricevuta!\n§7Assicurati di avere installato la mod ModWarden",
            "error.no_mod_checker", "§c[ModWarden] La mod ModWarden è obbligatoria!\n§7Scarica la mod per entrare nel server\n§7ID della mod: §fmodwarden",
            "error.unauthorized_mods", "§c[ModWarden] Rilevate mod non autorizzate:\n\n§7",
            "error.whitelist_mode", "Il server è in modalità §eWHITELIST§7.\nSono consentite solo le mod presenti nella configurazione.\n\n",
            "error.banlist_mode", "Il server è in modalità §eBLACKLIST§7.\nQueste mod non sono consentite sul server.\n\n",
            "error.remove_mods", "\n§fRimuovi queste mod per entrare nel server",
            "success.all_mods_checked", "§a[ModWarden] ✓ Tutte le mod verificate"),
        "es", Map.of(
            "error.no_mod_list", "§c[ModWarden] ¡No se recibió la lista de mods!\n§7Asegúrate de tener instalado el mod ModWarden",
            "error.no_mod_checker", "§c[ModWarden] ¡Se requiere el mod ModWarden!\n§7Descarga el mod para entrar al servidor\n§7ID del mod: §fmodwarden",
            "error.unauthorized_mods", "§c[ModWarden] Se detectaron mods no autorizados:\n\n§7",
            "error.whitelist_mode", "El servidor está en modo §eLISTA BLANCA§7.\nSolo se permiten los mods de la configuración.\n\n",
            "error.banlist_mode", "El servidor está en modo §eLISTA NEGRA§7.\nEstos mods no están permitidos en el servidor.\n\n",
            "error.remove_mods", "\n§fElimina estos mods para entrar al servidor",
            "success.all_mods_checked", "§a[ModWarden] ✓ Todos los mods verificados")
    );

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(ModListServer::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        int now = server.getTickCount();
        pendingChecks.entrySet().removeIf(entry -> {
            if (now - entry.getValue() <= TIMEOUT_TICKS) return false;
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player != null) {
                player.connection.disconnect(Component.literal(getMessage(player, "error.no_mod_list")));
            }
            return true;
        });
    }

    public static void processClientMods(ServerPlayer player, List<String> clientMods) {
        pendingChecks.remove(player.getUUID());

        if (!clientMods.contains(ModWarden.MOD_ID)) {
            player.connection.disconnect(Component.literal(getMessage(player, "error.no_mod_checker")));
            return;
        }

        List<String> forbiddenMods = clientMods.stream().filter(id -> !ModsConfig.isModAllowed(id)).toList();
        if (!forbiddenMods.isEmpty()) {
            StringBuilder message = new StringBuilder(getMessage(player, "error.unauthorized_mods"));
            message.append(getMessage(player, ModsConfig.getCurrentMode() == ModsConfig.Mode.WHITELIST
                ? "error.whitelist_mode" : "error.banlist_mode"));
            for (String modId : forbiddenMods) {
                message.append("• ").append(modId).append("\n");
            }
            message.append(getMessage(player, "error.remove_mods"));
            player.connection.disconnect(Component.literal(message.toString()));
            return;
        }

        player.sendSystemMessage(Component.literal(getMessage(player, "success.all_mods_checked")));
    }

    private static String getMessage(ServerPlayer player, String key) {
        String locale = "en_us";
        try {
            locale = player.clientInformation().language();
        } catch (Exception e) {
            ModWarden.LOGGER.warn("Error getting player locale: {}", e.getMessage());
        }
        return message(locale, key);
    }

    /** Picks the message for a client locale like "fr_fr", falling back to English. */
    static String message(String locale, String key) {
        String lang = locale == null ? "en" : locale.toLowerCase(Locale.ROOT).split("_")[0];
        return MESSAGES.getOrDefault(lang, MESSAGES.get("en")).getOrDefault(key, key);
    }

    public static void scheduleTimeoutCheck(ServerPlayer player, MinecraftServer server) {
        pendingChecks.put(player.getUUID(), server.getTickCount());
    }
}
