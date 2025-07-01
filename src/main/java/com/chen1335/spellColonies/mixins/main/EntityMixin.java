package com.chen1335.spellColonies.mixins.main;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"), cancellable = true)
    private void isAlliedTo(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IMagicSummon summon && summon.getSummoner() != null) {
            if (entity instanceof Player player) {
                LivingEntity summonerOwner = summon.getSummoner();
                if (summonerOwner instanceof AbstractEntityCitizen citizen) {
                    ICitizenData citizenData = citizen.getCitizenData();
                    if (citizenData != null) {
                        @NotNull List<Player> players = citizenData.getColony().getImportantMessageEntityPlayers();
                        if (players.contains(player)) {
                            cir.setReturnValue(true);
                        }
                    }
                }
            } else if (entity instanceof AbstractEntityCitizen) {
                cir.setReturnValue(true);
            }
        } else if ((Object) this instanceof AbstractEntityCitizen && entity instanceof Player) {
            cir.setReturnValue(true);
        }
    }
}
