package com.chen1335.spellColonies.network;

import com.chen1335.spellColonies.SpellColonies;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.netty.buffer.ByteBuf;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CitizenPlayAnimationPack(int entityId, ResourceLocation animationLocation,
                                       int animationType) implements CustomPacketPayload {
    public static final Type<CitizenPlayAnimationPack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SpellColonies.MODID, "citizen_play_animation"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, CitizenPlayAnimationPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            CitizenPlayAnimationPack::entityId,
            ResourceLocation.STREAM_CODEC,
            CitizenPlayAnimationPack::animationLocation,
            ByteBufCodecs.INT,
            CitizenPlayAnimationPack::animationType,
            CitizenPlayAnimationPack::new
    );


    public void handler(IPayloadContext iPayloadContext) {
        if (iPayloadContext.player().level().getEntity(entityId) instanceof AbstractEntityCitizen citizen) {
            IAnimatedPlayer animatedPlayer = (IAnimatedPlayer) citizen;
            if (AnimationType.values()[animationType] == AnimationType.IRON_SPELL_BOOK) {
                @Nullable IPlayable toPlayAnimation = PlayerAnimationRegistry.getAnimation(animationLocation);
                @Nullable ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) animatedPlayer.playerAnimator_getAnimation(SpellAnimations.ANIMATION_RESOURCE);
                if (animationLocation.equals(SpellColonies.EMPTY_ANIMATION)) {
                    animation.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(2, Ease.INOUTSINE), null);
                } else {
                    animation.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(2, Ease.INOUTSINE), toPlayAnimation.playAnimation());
                }
            }
        }
    }


    public enum AnimationType {
        IRON_SPELL_BOOK
    }
}
