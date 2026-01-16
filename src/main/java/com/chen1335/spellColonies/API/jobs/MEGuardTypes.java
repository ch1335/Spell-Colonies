package com.chen1335.spellColonies.API.jobs;

import com.chen1335.spellColonies.SpellColonies;
import com.chen1335.spellColonies.ai.jobs.JobMagician;
import com.minecolonies.api.colony.guardtype.GuardType;
import com.minecolonies.api.colony.guardtype.registry.ModGuardTypes;
import com.minecolonies.api.entity.citizen.Skill;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class MEGuardTypes {
    public final static DeferredRegister<GuardType> DEFERRED_REGISTER = DeferredRegister.create(ResourceLocation.tryBuild("minecolonies", "guardtypes"), SpellColonies.MODID);

    public static final ResourceLocation MAGICIAN_ID = ResourceLocation.fromNamespaceAndPath(SpellColonies.MODID, "magician");

    public static RegistryObject<GuardType> magician = DEFERRED_REGISTER.register(ModGuardTypes.KNIGHT_ID.getPath(), () -> new GuardType.Builder()
            .setJobTranslationKey("com.minecolonies_extension.job.magician")
            .setButtonTranslationKey("com.minecolonies_extension.coremod.gui.workerhuts.magician")
            .setPrimarySkill(Skill.Intelligence)
            .setSecondarySkill(Skill.Mana)
            .setWorkerSoundName("magician")
            .setJobEntry(() -> MEJobs.magician.get())
            .setRegistryName(MEGuardTypes.MAGICIAN_ID)
            .setClazz(JobMagician.class)
            .createGuardType());

}
