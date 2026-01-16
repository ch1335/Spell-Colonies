package com.chen1335.spellColonies.hooks;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenSkillHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

public class CitizenSkillHandlerMixinHooks {

    public static void onLevelUp(CitizenSkillHandler citizenSkillHandler, Skill skill, double xp, ICitizenData data, CitizenSkillHandler.SkillData skillData, CallbackInfo ci) {
        for (Player player : data.getColony().getImportantMessageEntityPlayers()) {
            player.sendSystemMessage(Component.translatable("spell_colonies.info.skillLevelUp", data.getName(), Component.translatable("com.minecolonies.coremod.gui.citizen.skills." + skill.name().toLowerCase(Locale.US)), skillData.getLevel()).withStyle(ChatFormatting.GRAY));
        }
    }
}
