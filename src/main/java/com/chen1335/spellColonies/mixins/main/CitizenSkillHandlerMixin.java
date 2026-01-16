package com.chen1335.spellColonies.mixins.main;

import com.llamalad7.mixinextras.sugar.Local;
import com.minecolonies.api.colony.ICitizen;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenSkillHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CitizenSkillHandler.class,remap = false)
public abstract class CitizenSkillHandlerMixin implements ICitizen {
    @Inject(method = "addXpToSkill", at = @At(value = "INVOKE", target = "Lcom/minecolonies/core/entity/citizen/citizenhandlers/CitizenSkillHandler;levelUp(Lcom/minecolonies/api/colony/ICitizenData;)V"))
    private void onLevelUp(Skill skill, double xp, ICitizenData data, CallbackInfo ci, @Local CitizenSkillHandler.SkillData skillData) {
//        CitizenSkillHandlerMixinHooks.onLevelUp((CitizenSkillHandler) (Object) this, skill, xp, data, skillData, ci);
    }
}
