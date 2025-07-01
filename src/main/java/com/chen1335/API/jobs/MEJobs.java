package com.chen1335.API.jobs;

import com.chen1335.spellColonies.SpellColonies;
import com.chen1335.spellColonies.ai.jobs.JobMagician;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.apiimp.CommonMinecoloniesAPIImpl;
import com.minecolonies.core.colony.jobs.views.DefaultJobView;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MEJobs {
    public final static DeferredRegister<JobEntry> DEFERRED_REGISTER = DeferredRegister.create(CommonMinecoloniesAPIImpl.JOBS, SpellColonies.MODID);


    public static final ResourceLocation MAGICIAN_ID = ResourceLocation.fromNamespaceAndPath(SpellColonies.MODID, "magician");

    public static DeferredHolder<JobEntry, JobEntry> magician = register(DEFERRED_REGISTER, MEJobs.MAGICIAN_ID.getPath(), () -> new JobEntry.Builder()
            .setJobProducer(JobMagician::new)
            .setJobViewProducer(() -> DefaultJobView::new)
            .setRegistryName(MEJobs.MAGICIAN_ID)
            .createJobEntry());

    private static DeferredHolder<JobEntry, JobEntry> register(final DeferredRegister<JobEntry> deferredRegister, final String path, final Supplier<JobEntry> supplier) {
        ModJobs.jobs.add(ResourceLocation.fromNamespaceAndPath(SpellColonies.MODID, path));
        return deferredRegister.register(path, supplier);
    }
}

