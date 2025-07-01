package com.chen1335.spellColonies.buildings.modules;

import com.chen1335.API.jobs.MEGuardTypes;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.core.colony.buildings.modules.GuardBuildingModule;
import com.minecolonies.core.colony.buildings.moduleviews.CombinedHiringLimitModuleView;

public class SCBuildingModules {
    public static final BuildingEntry.ModuleProducer<GuardBuildingModule, CombinedHiringLimitModuleView> MAGICIAN_TOWER_WORK =
            new BuildingEntry.ModuleProducer<>("magician_tower_work", () -> new GuardBuildingModule(MEGuardTypes.magician.get(), true, (b) -> 1), () -> CombinedHiringLimitModuleView::new);

}
