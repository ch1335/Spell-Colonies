package com.chen1335.spellColonies.client.animation;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface CitizenAnimationFactory {
    FactoryHolder ANIMATION_DATA_FACTORY = new FactoryHolder();

    @Nullable IAnimation invoke(@NotNull AbstractEntityCitizen player);


    class FactoryHolder {
        private FactoryHolder() {
        }

        private static final List<Function<AbstractEntityCitizen, DataHolder>> factories = new ArrayList<>();

        public void registerFactory(@Nullable ResourceLocation id, int priority, @NotNull CitizenAnimationFactory factory) {
            factories.add(player -> Optional.ofNullable(factory.invoke(player)).map(animation -> new DataHolder(id, priority, animation)).orElse(null));
        }

        @ApiStatus.Internal
        private record DataHolder(@Nullable ResourceLocation id, int priority, @NotNull IAnimation animation) {
        }

        @ApiStatus.Internal
        public void prepareAnimations(AbstractEntityCitizen player, AnimationStack playerStack, Map<ResourceLocation, IAnimation> animationMap) {
            for (Function<AbstractEntityCitizen, DataHolder> factory : factories) {
                DataHolder dataHolder = factory.apply(player);
                if (dataHolder != null) {
                    playerStack.addAnimLayer(dataHolder.priority(), dataHolder.animation());
                    if (dataHolder.id() != null) {
                        animationMap.put(dataHolder.id(), dataHolder.animation());
                    }
                }
            }
        }
    }
}
