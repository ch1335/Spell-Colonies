package com.chen1335.spellColonies.network;

import com.chen1335.spellColonies.SpellColonies;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.util.ClientUtils;

import java.util.function.Supplier;

public record CitizenPlayAnimationPack(int entityId, ResourceLocation animationLocation, int animationType) {


    public static CitizenPlayAnimationPack decoder(FriendlyByteBuf friendlyByteBuf) {
        int entityId = friendlyByteBuf.readInt();
        ResourceLocation animationLocation = friendlyByteBuf.readResourceLocation();
        int animationType = friendlyByteBuf.readInt();
        return new CitizenPlayAnimationPack(entityId, animationLocation, animationType);
    }

    public void handel(Supplier<NetworkEvent.Context> contextSupplier) {
        if (ClientUtils.getClientPlayer().level().getEntity(entityId) instanceof AbstractEntityCitizen citizen) {
            IAnimatedPlayer animatedPlayer = (IAnimatedPlayer) citizen;
            if (AnimationType.values()[animationType] == AnimationType.IRON_SPELL_BOOK) {
                @Nullable KeyframeAnimation toPlayAnimation = PlayerAnimationRegistry.getAnimation(animationLocation);
                @Nullable ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) animatedPlayer.playerAnimator_getAnimation(SpellAnimations.ANIMATION_RESOURCE);
                if (animationLocation.equals(SpellColonies.EMPTY_ANIMATION)) {
                    animation.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(2, Ease.INOUTSINE), null);
                } else {

                    animation.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(2, Ease.INOUTSINE), new KeyframeAnimationPlayer(toPlayAnimation));
                }
            }
        }
    }

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(entityId);
        friendlyByteBuf.writeResourceLocation(animationLocation);
        friendlyByteBuf.writeInt(animationType);
    }


    public enum AnimationType {
        IRON_SPELL_BOOK
    }
}
