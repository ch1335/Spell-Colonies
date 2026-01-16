package com.chen1335.spellColonies.network;


import com.chen1335.spellColonies.SpellColonies;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int PacketId = 0;

    private static int id() {
        return PacketId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.newSimpleChannel(ResourceLocation.tryBuild(SpellColonies.MODID, "messages"), () -> "1.0", (s) -> true, (s) -> true);
        INSTANCE = net;

        net.messageBuilder(CitizenPlayAnimationPack.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CitizenPlayAnimationPack::decoder)
                .encoder(CitizenPlayAnimationPack::write)
                .consumerMainThread(CitizenPlayAnimationPack::handel)
                .add();
    }

    public static <M> void sendToServer(M message) {
        INSTANCE.sendToServer(message);
    }

    public static <M> void sendToClient(M message, ServerPlayer serverPlayer) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
    }

    public static <M> void broadcast(Entity entity, M message) {
        entity.level().players().forEach(player -> {
            if (player.distanceToSqr(entity) <= 10000) {
                INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
            }
        });


    }
}
