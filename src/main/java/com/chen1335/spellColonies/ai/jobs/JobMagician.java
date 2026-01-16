package com.chen1335.spellColonies.ai.jobs;

import com.chen1335.spellColonies.ai.workers.guard.EntityAIMagician;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIGuard;

public class JobMagician extends AbstractJobGuard<JobMagician> {
    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobMagician(ICitizenData entity) {
        super(entity);
    }

    @Override
    protected AbstractEntityAIGuard<JobMagician, ? extends AbstractBuildingGuards> generateGuardAI() {
        return new EntityAIMagician(this);
    }
}
