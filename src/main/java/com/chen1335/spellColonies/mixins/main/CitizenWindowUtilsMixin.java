package com.chen1335.spellColonies.mixins.main;

import com.ldtteam.blockui.controls.Image;
import com.llamalad7.mixinextras.sugar.Local;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.core.client.gui.citizen.CitizenWindowUtils;
import com.minecolonies.core.client.gui.citizen.JobWindowCitizen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.minecolonies.api.util.constant.WindowConstants.*;

@Mixin(value = CitizenWindowUtils.class,remap = false)
public class CitizenWindowUtilsMixin {
    @Inject(method = "updateJobPage", at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/entity/citizen/Skill;getComplimentary()Lcom/minecolonies/api/entity/citizen/Skill;", ordinal = 0))
    private static void onUpdateJobPageFix1(ICitizenDataView citizen, JobWindowCitizen windowCitizen, IColonyView colony, CallbackInfo ci, @Local(name = "primary") Skill primary) {
        if (primary == Skill.Intelligence) {
            windowCitizen.findPaneOfTypeByID(PRIMARY_SKILL_COM + IMAGE_APPENDIX, Image.class).hide();
            windowCitizen.findPaneOfTypeByID(PRIMARY_SKILL_ADV + IMAGE_APPENDIX, Image.class).hide();
        }
    }

    @Inject(method = "updateJobPage", at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/entity/citizen/Skill;getComplimentary()Lcom/minecolonies/api/entity/citizen/Skill;", ordinal = 3))
    private static void onUpdateJobPageFix2(ICitizenDataView citizen, JobWindowCitizen windowCitizen, IColonyView colony, CallbackInfo ci, @Local(name = "secondary") Skill secondary) {
        if (secondary == Skill.Intelligence) {
            windowCitizen.findPaneOfTypeByID(SECONDARY_SKILL_COM + IMAGE_APPENDIX, Image.class).hide();
            windowCitizen.findPaneOfTypeByID(SECONDARY_SKILL_ADV + IMAGE_APPENDIX, Image.class).hide();
        }
    }
}
