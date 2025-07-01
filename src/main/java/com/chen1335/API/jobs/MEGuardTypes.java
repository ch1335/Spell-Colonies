package com.chen1335.API.jobs;

import com.chen1335.spellColonies.SpellColonies;
import com.chen1335.spellColonies.ai.jobs.JobMagician;
import com.minecolonies.api.colony.guardtype.GuardType;
import com.minecolonies.api.colony.guardtype.registry.ModGuardTypes;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.apiimp.CommonMinecoloniesAPIImpl;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MEGuardTypes {
    public final static DeferredRegister<GuardType> DEFERRED_REGISTER = DeferredRegister.create(CommonMinecoloniesAPIImpl.GUARD_TYPES, SpellColonies.MODID);

    public static final ResourceLocation MAGICIAN_ID = ResourceLocation.fromNamespaceAndPath(SpellColonies.MODID, "magician");

    public static DeferredHolder<GuardType, GuardType> magician = DEFERRED_REGISTER.register(ModGuardTypes.KNIGHT_ID.getPath(), () -> new GuardType.Builder()
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
