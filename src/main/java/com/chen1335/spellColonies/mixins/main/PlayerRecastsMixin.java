package com.chen1335.spellColonies.mixins.main;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerRecasts.class,remap = false)
public abstract class PlayerRecastsMixin {

    @Shadow
    @Final
    private ServerPlayer serverPlayer;

    @Inject(method = "triggerRecastComplete", at = @At("HEAD"), cancellable = true)
    private void triggerRecastComplete(RecastInstance recastInstance, RecastResult recastResult, CallbackInfo ci) {
        if (serverPlayer == null) {
            ci.cancel();
        }
    }
}
