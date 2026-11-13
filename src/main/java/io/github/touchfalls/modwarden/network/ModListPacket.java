package io.github.touchfalls.modwarden.network;

import io.github.touchfalls.modwarden.ModWarden;
import io.github.touchfalls.modwarden.server.ModListServer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;

public record ModListPacket(List<String> mods) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ModListPacket> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModWarden.MOD_ID, "mod_list"));
    public static final StreamCodec<FriendlyByteBuf, ModListPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
        ModListPacket::mods,
        ModListPacket::new
    );

    public static void registerServerReceiver() {
        PayloadTypeRegistry.serverboundPlay().register(TYPE, CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) ->
            context.server().execute(() -> ModListServer.processClientMods(context.player(), payload.mods())));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
