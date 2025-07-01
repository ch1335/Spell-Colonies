package com.chen1335.spellColonies.mixins.main;

import com.chen1335.spellColonies.mixins.FactoryHolderAccessor;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Mixin(PlayerAnimationFactory.FactoryHolder.class)
public abstract class PlayerAnimationFactory$FactoryHolderMixin implements FactoryHolderAccessor {
    @Accessor("factories")
    public abstract List<Function<Objects, Objects>> getFactories();
}
