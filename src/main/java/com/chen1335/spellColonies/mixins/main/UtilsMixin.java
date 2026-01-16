package com.chen1335.spellColonies.mixins.main;

import com.chen1335.spellColonies.ai.workers.guard.EntityAIMagician;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(value = Utils.class,remap = false)
public class UtilsMixin {
    @Inject(method = "internalRaycastForEntity", at = @At("HEAD"))
    private static void internalRaycastForEntity(Level level, Entity originEntity, Vec3 start, Vec3 end, boolean checkForBlocks, float bbInflation, Predicate<? super Entity> filter, CallbackInfoReturnable<HitResult> cir, @Local(argsOnly = true) LocalRef<Predicate<Entity>> predicateLocalRef) {
        Predicate<Entity> old = predicateLocalRef.get();
        predicateLocalRef.set(living -> {
            if (living instanceof AbstractEntityCitizen) {
                return false;
            }

            if (living instanceof Player player && originEntity instanceof AbstractEntityCitizen entityCitizen) {
                IJob<?> job = entityCitizen.getCitizenJobHandler().getColonyJob();
                if (job != null && job.getWorkerAI() instanceof EntityAIMagician aiMagician && aiMagician.magicianCombatAI.getCurrentTarget() != player) {
                    return false;
                }
            }

            return old.test(living);
        });
    }
}
